package eneseminozbilgin;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Hospital implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
    private String name;
    private List<Section> sections;

    public Hospital(int id, String name) {
        this.id = id;
        this.name = name;
        sections = new LinkedList<Section>();
    }
    
    public void addSection(Section section) throws DuplicateInfoException {
    	if(sections.contains(section)) {
    		if (!AppConfig.getGuiMode()) {
    			throw new DuplicateInfoException("This section has already been added.");
            } else {
                System.out.println("Duplicate section detected but ignored in GUI mode.");
                return;   
            }
    	} 
    	else {
    		sections.add(section);
    	}
    }

    public Section getSection(int id) {
        for(Section e : sections) {
        	if(e.getId() == id) {
        		return e;
        	}
        }
        return null;
    }
    @SuppressWarnings("unused")
	private Section getSection(String name) {
    	for(Section e : sections) {
        	if(e.getName().equals(name)) {
        		return e;
        	}
        }
        return null;
    }
    public String toString() {
		return "Hospital Id: " + id + "\nHospital Name: " + name + "\nNumber of Sections : " + sections.size();
	}
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public int getId() {
		return id;
	}
}

