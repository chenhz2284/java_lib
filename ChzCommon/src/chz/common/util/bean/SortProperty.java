package chz.common.util.bean;

/*
 * ��ʾ��Ҫ���������
 */
public class SortProperty {
	
	public static final int sort_type_asc = 1;		// ����
	public static final int sort_type_desc = -1;	// ����
	
	//------------

	private String property;
	private int sortType = sort_type_asc;			// Ĭ��������
	
	//-----------
	
	public SortProperty(String property){
		this.property = property;
	}
	
	public SortProperty(String property, int sortType){
		this.property = property;
		this.setSortType(sortType);
	}
	
	//-------------
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public int getSortType() {
		return sortType;
	}
	public void setSortType(int sortType) {
		if( sortType!=sort_type_asc && sortType!=sort_type_desc ){
			throw new RuntimeException("invalid value! must be SortProperty.sort_type_asc or SortProperty.sort_type_desc");
		}
		this.sortType = sortType;
	}
	
	//--------------
}
