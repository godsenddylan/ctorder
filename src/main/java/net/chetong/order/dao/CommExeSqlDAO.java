package net.chetong.order.dao;

import java.util.List;

import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.page.domain.PageBounds;
import net.chetong.order.util.page.domain.PageList;

 /**
  * 公共DAO类
  * @author wufeng@chetong.net
  * @creation 2015年11月6日
  */

public interface CommExeSqlDAO {
	
	/**
	 * 公共查询方法，所以查询都能用，返回List
	 * @param sqlstm_id
	 * @param params
	 * @return
	 */
	public <T> List<T> queryForList(String sqlstm_id,Object params) throws DaoException;
	
	/**
	 * 公共查询方法，查询单个，返回Object
	 * @param sqlstm_id
	 * @param params
	 * @return 
	 * @return
	 */
	public <T> T queryForObject(String sqlstm_id,Object params) throws DaoException;
	
	/**
	 * 公共写入数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param o
	 * @return
	 */
	public int insertVO(String sqlstm_id,Object o) throws DaoException;
	
	/**
	 * 公共批量写入数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param l
	 * @return
	 */
	public <T> int insertBatchVO(String sqlstm_id,List<T> l) throws DaoException;
	
	/**
	 * 公共更新数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param o
	 * @return
	 */
	public int updateVO(String sqlstm_id,Object o) throws DaoException;
	
	/**
	 * 公共更新写入数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param l
	 * @return
	 */
	public <T> int updateBatchVO(String sqlstm_id,List<T> l) throws DaoException;
	
	/**
	 * 公共删除数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param o
	 * @return
	 */
	public int deleteVO(String sqlstm_id,Object o) throws DaoException;
	
	/**
	 * 公共删除写入数据方法，写入单个数据，返回写入是否成功
	 * @param id
	 * @param l
	 * @return
	 */
	public <T> int deleteBatchVO(String sqlstm_id,List<T> l) throws DaoException;
	
	/**
	 * 分页查询
	 * @param sqlstm_id 
	 * @param o 查询参数
	 * @param page
	 * @return
	 * @throws DaoException
	 */
	public <T> PageList<T> queryForPage(String sqlstm_id,Object o,PageBounds page) throws DaoException;
	

}
