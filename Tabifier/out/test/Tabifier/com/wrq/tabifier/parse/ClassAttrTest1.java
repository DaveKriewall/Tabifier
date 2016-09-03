import java.util.List;
import java.util.Map;

public class ClassAttrTest1 {

	private Type m_type = null;
	private String m_string = null;
	private List<String> m_list = null;
	private Map<String, TabifierBug> m_map = null;
	private List<Map<String, TabifierBug>> m_mapList = null;


	public enum Type {
		String{
			public String getValue(TabifierBug value){
				return value.getString();
			}
		}, 
		List {
			public List<String> getValue(TabifierBug value){
				return value.getList();
			}
		}, 	
		Map {
			public Map<String, TabifierBug> getValue(TabifierBug value){
				return value.getMap();
			}
		}, 	
		MapList {
			public List<Map<String, TabifierBug>> getValue(TabifierBug value){
				return value.getMapList();
			}
		};

		public abstract Object getValue(TabifierBug value);
	}

	public Type getType() {
		return m_type;
	}

	public String getString() {
		return m_string;
	}

	public void setString(String value) {
		m_type = Type.String;		
		m_string = value;
		m_list = null;
		m_map = null;
		m_mapList = null;
	}

	public List<String> getList() {
		return m_list;
	}

	public void setList(List<String> value) {
		m_type = Type.List;
		m_string = null;
		m_list = value;
		m_map = null;
		m_mapList = null;
	}

	public Map<String, TabifierBug> getMap() {
		return m_map;
	}

	public void setMap(Map<String, TabifierBug> value) {
		m_type = Type.Map;
		m_string = null;
		m_list = null;
		m_map = value;
		m_mapList = null;
	}

	public List<Map<String, TabifierBug>> getMapList() {
		return m_mapList;
	}

	public void setMapList(List<Map<String, TabifierBug>> value) {
		m_type = Type.MapList;
		m_string = null;
		m_list = null;
		m_map = null;
		m_mapList = value;
	}
}
