package dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public abstract class BaseDao <T>{
	private SessionFactory sessionFactory;
	private Configuration cfg;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	//在没有使用Spring之前，需要自己创建一个SessionFactory
	public BaseDao() {
		super();
		cfg = new Configuration().configure();
        StandardServiceRegistry build = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
        sessionFactory = cfg.buildSessionFactory(build);
	}
	
	@SuppressWarnings("unchecked")
	public T get(Class<T> entityClass,Serializable id){
		return (T) sessionFactory.openSession().get(entityClass, id);
	}
	
	public Serializable save(T entity){
		return sessionFactory.openSession().save(entity);
	}
	
	public void update(T entity){
		getSessionFactory().openSession().saveOrUpdate(entity);
	}
	
	public void delete(T entity){
		 getSessionFactory().openSession().delete(entity);
	}
	
	@SuppressWarnings("unchecked")
	public boolean delete(Class<T> entityClass,Serializable id){
		T entity = (T) getSessionFactory().openSession().get(entityClass, id);
		if(entity != null){
			getSessionFactory().openSession().delete(entity);
			return true;
		}
		else return false;
	}
	
	// 获取所有实体
	public List<T> findAll(Class<T> entityClazz)
	{
		return find("select en from "
			+ entityClazz.getSimpleName() + " en");
	}
	
	// 获取实体总数
	public long findCount(Class<T> entityClazz)
	{
		List<?> list = find("select count(*) from "
			+ entityClazz.getSimpleName());
		// 返回查询得到的实体总数
		if (list != null && list.size() == 1 )
		{
			return (Long)list.get(0);
		}
		return 0;
	}

	// 根据HQL语句查询实体
	@SuppressWarnings("unchecked")
	protected List<T> find(String hql)
	{
		return (List<T>)getSessionFactory().openSession()
			.createQuery(hql)
			.list();
	}
	// 根据带占位符参数HQL语句查询实体
	@SuppressWarnings("unchecked")
	protected List<T> find(String hql , Object... params)
	{
		// 创建查询
		Query query = getSessionFactory().openSession()
			.createQuery(hql);
		// 为包含占位符的HQL语句设置参数
		for(int i = 0 , len = params.length ; i < len ; i++)
		{
			query.setParameter(i + "" , params[i]);
		}
		return (List<T>)query.list();
	}
	/**
	 * 使用hql 语句进行分页查询操作
	 * @param hql 需要查询的hql语句
	 * @param pageNo 查询第pageNo页的记录
	 * @param pageSize 每页需要显示的记录数
	 * @return 当前页的所有记录
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByPage(String hql,
		 int pageNo, int pageSize)
	{
		// 创建查询
		return getSessionFactory().openSession()
			.createQuery(hql)
			// 执行分页
			.setFirstResult((pageNo - 1) * pageSize)
			.setMaxResults(pageSize)
			.list();
	}
	/**
	 * 使用hql 语句进行分页查询操作
	 * @param hql 需要查询的hql语句
	 * @param params 如果hql带占位符参数，params用于传入占位符参数
	 * @param pageNo 查询第pageNo页的记录
	 * @param pageSize 每页需要显示的记录数
	 * @return 当前页的所有记录
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByPage(String hql , int pageNo, int pageSize
		, Object... params)
	{
		// 创建查询
		Query query = getSessionFactory().openSession()
			.createQuery(hql);
		// 为包含占位符的HQL语句设置参数
		for(int i = 0 , len = params.length ; i < len ; i++)
		{
			query.setParameter(i + "" , params[i]);
		}
		// 执行分页，并返回查询结果
		return query.setFirstResult((pageNo - 1) * pageSize)
			.setMaxResults(pageSize)
			.list();
	}
}