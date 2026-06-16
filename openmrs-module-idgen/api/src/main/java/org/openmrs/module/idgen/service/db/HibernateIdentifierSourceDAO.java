/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.service.db;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.EmptyIdentifierPoolException;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Hibernate Implementation of the IdentifierSourceDAO Interface
 */
@Transactional
public class HibernateIdentifierSourceDAO implements IdentifierSourceDAO {
	
	protected Log log = LogFactory.getLog(getClass());

	private static final String IDENTIFIER_TYPE = "identifierType";
	private static final String DATE_GENERATED = "dateGenerated";
	
	//***** PROPERTIES *****
	
	private DbSessionFactory sessionFactory;
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	@Transactional(readOnly=true)
	@Override
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return (IdentifierSource) sessionFactory.getCurrentSession().get(IdentifierSource.class, id);
	}

	/** 
	 * @see IdentifierSourceDAO#getAllIdentifierSources(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	@Override
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IdentifierSource.class);
		if (!includeRetired) {
			criteria.add(Expression.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
	 */
	@Transactional
	@Override
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException {
		DbSession currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(identifierSource);
		currentSession.flush();
		refreshIdentifierSource(identifierSource);
		return identifierSource;
	}

	/** 
	 * @see IdentifierSourceService#purgeIdentifierSource(IdentifierSource)
	 */
	@Transactional
	@Override
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		sessionFactory.getCurrentSession().delete(identifierSource);
	}
	
	/**
	 * 
	 * @see IdentifierSourceDAO#getAvailableIdentifiers(IdentifierPool, int)
	 */
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	@Override
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PooledIdentifier.class);
		criteria.add(Expression.isNull("dateUsed"));
		criteria.add(Expression.eq("pool", pool));
		criteria.setMaxResults(quantity);
		if (pool.isSequential()) {
			criteria.addOrder(Order.asc("identifier"));
		}
		else {
			criteria.addOrder(Order.asc("uuid"));
		}
		List<PooledIdentifier> results = (List<PooledIdentifier>) criteria.list();
		if (results.size() < quantity) {
			throw new EmptyIdentifierPoolException("Unable to retrieve " + quantity + " available identifiers from Pool " + pool + ".  Maybe you need to add more identifiers to your pool first.");
		}
		return results;
	}
	
	/**
	 * @see IdentifierSourceDAO#getQuantityInPool(IdentifierPool, boolean, boolean)
	 */
	@Transactional(readOnly=true)
	@Override
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) {
		String hql = "select count(*) from PooledIdentifier where pool_id = " + pool.getId();
		if (availableOnly) {
			hql += " and date_used is null";
		}
		if (usedOnly) {
			hql += " and date_used is not null";
		}
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		Number result = (Number) query.uniqueResult();
		return result != null ? result.intValue() : 0;
	}

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(Integer)
     */
    @Transactional(readOnly=true)
    @Override
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq("id", autoGenerationOptionId));
        return (AutoGenerationOption)criteria.uniqueResult();
    }

    /**
	 * @see IdentifierSourceService#getAutoGenerationOptionByUuid(String)
	 */
    @Override
	public AutoGenerationOption getAutoGenerationOptionByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq("uuid", uuid));
        return (AutoGenerationOption)criteria.uniqueResult();
	}
    
    /**
	 * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType,Location)
	 */
	@Override
	@Transactional(readOnly=true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws APIException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
		criteria.add(Expression.eq(IDENTIFIER_TYPE, type));
        criteria.add(Restrictions.or(Expression.eq("location", location), Expression.isNull("location")));
		return (AutoGenerationOption) criteria.uniqueResult();
	}

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
     */
    @Override
    @Transactional(readOnly=true)
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws APIException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq(IDENTIFIER_TYPE, type));
        return (List<AutoGenerationOption>) criteria.list();
    }

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
     */
    @Override
    @Transactional(readOnly=true)
    public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws APIException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq(IDENTIFIER_TYPE, type));
        return (AutoGenerationOption)criteria.uniqueResult();
    }

	/** 
	 * @see IdentifierSourceDAO#saveAutoGenerationOption(AutoGenerationOption)
	 */
	@Override
	@Transactional
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws APIException {
		sessionFactory.getCurrentSession().saveOrUpdate(option);
		return option;
	}

	/** 
	 * @see IdentifierSourceDAO#purgeAutoGenerationOption(AutoGenerationOption)
	 */
	@Override
	@Transactional
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws APIException {
		sessionFactory.getCurrentSession().delete(option);
	}

	/** 
	 * @see IdentifierSourceDAO#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LogEntry.class);
		if (source != null) {
			criteria.add(Expression.eq("source", source));
		}
		if (fromDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			criteria.add(Expression.ge(DATE_GENERATED, fromDate));
		}
		if (toDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(toDate);
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			criteria.add(Expression.lt(DATE_GENERATED, c.getTime()));
		}
		if (identifier != null) {
			criteria.add(Expression.like("identifier", identifier, MatchMode.ANYWHERE));
		}	
		if (generatedBy != null) {
			criteria.add(Expression.eq("generatedBy", generatedBy));
		}
		if (comment != null) {
			criteria.add(Expression.like("comment", comment, MatchMode.ANYWHERE));
		}	
		criteria.addOrder(Order.desc(DATE_GENERATED));
		return (List<LogEntry>) criteria.list();
	}

	/**
	 * @see IdentifierSourceDAO#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public LogEntry getMostRecentLogEntry(IdentifierSource source) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LogEntry.class);
		if (source == null) {
			throw new DAOException("You must specify the Identifier Source that you wish to query");
		}
		criteria.add(Restrictions.eq("source", source));
		criteria.addOrder(Order.desc(DATE_GENERATED));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);
		return (LogEntry) criteria.uniqueResult();
	}

    /**
     * @see IdentifierSourceDAO#getIdentifierSourceByUuid(String)
     */
    @Override
    public IdentifierSource getIdentifierSourceByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IdentifierSource.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (IdentifierSource) criteria.uniqueResult();
    }
    
    /**
     * @see IdentifierSourceDAO#getIdentifierSourcesByType(PatientIdentifierType)
     */
    @Override
    public List<IdentifierSource> getIdentifierSourcesByType(PatientIdentifierType patientIdentifierType) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IdentifierSource.class);
        criteria.add(Expression.eq(IDENTIFIER_TYPE, patientIdentifierType));
        criteria.add(Expression.like("retired", false));
        return (List<IdentifierSource>) criteria.list();
    }    

	/**
	 * @see org.openmrs.module.idgen.service.db.IdentifierSourceDAO#saveLogEntry(LogEntry)
	 */
	@Override
	public LogEntry saveLogEntry(LogEntry logEntry) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(logEntry);
		return logEntry;
	}

    /**
     * @see IdentifierSourceDAO#saveSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator, long)
     */
    @Override
    public void saveSequenceValue(SequentialIdentifierGenerator generator, long sequenceValue) {
        int updated = sessionFactory.getCurrentSession()
                .createSQLQuery("update idgen_seq_id_gen set next_sequence_value = :val where id = :id")
                .setParameter("val", sequenceValue)
                .setParameter("id", generator.getId())
                .executeUpdate();
        if (updated != 1) {
            throw new APIException("Expected to update 1 row but updated " + updated + " rows instead!");
        }
    }

    /**
     * @see IdentifierSourceDAO#getSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator)
     */
    @Override
    public Long getSequenceValue(SequentialIdentifierGenerator generator) {
        Number val = (Number) sessionFactory.getCurrentSession()
                .createSQLQuery("select next_sequence_value from idgen_seq_id_gen where id = :id")
		        // Added IntegerType.INSTANCE because hibernate in case of PostgreSQL converts null ids to 
		        // bytea type and causes error. So had to add explicit type.
		        .setParameter("id", generator.getId(), IntegerType.INSTANCE)
                .uniqueResult();
        return val == null ? null : val.longValue();
	}

    @Override
    public void refreshIdentifierSource(IdentifierSource source) {
        sessionFactory.getCurrentSession().refresh(source);
    }

    /**
     * {@inheritDoc}
     * WARNING: hql parameter is concatenated directly into the query — vulnerable to HQL injection.
     * This is a known finding (SAST-04) documented in Groep_6_Security-Analyse.md.
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Object> executeHqlQuery(String hql) {
        return sessionFactory.getCurrentSession().createQuery(hql).list();
    }



	//***** PROPERTY ACCESS *****

	/**
	 * @return the sessionFactory
	 */
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
