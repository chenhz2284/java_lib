package chz.common.util.common;

import java.util.ArrayList;
import java.util.List;

public class PageUtil<clazz> {

	private List<clazz> list;
	public int pageSize = 100;
	public int pageCount = 0;
	
	public PageUtil(List<clazz> list){
		this.list = list;
		pageCount = this.list.size() / pageSize + 1;
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
		pageCount = this.list.size() / pageSize + 1;
	}
	
	/*
	 * pageNum «¥”0À„∆
	 */
	public List<clazz> getPageInfo(int pageNum) throws InstantiationException, IllegalAccessException{
		int beginIndex = pageNum*pageSize;
		int resultSize = ( list.size()-beginIndex > pageSize ) ? pageSize : list.size()-beginIndex;
		List<clazz> result = new ArrayList<clazz>(resultSize);  // new clazz[resultSize];
		for( int i=beginIndex; i<beginIndex+resultSize; i++  ){
			result.add(list.get(i));
		}
		return result;
	}
	
}
