package modules.admin.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.futurepages.core.auth.DefaultRole;

@Entity
public class Role implements DefaultRole, Serializable{
    
    @Id
    @Column(length=100,nullable=false)
    private String roleId;
    
    @Column(length=100,nullable=false)    
    private String title;
    
	@ManyToOne
	private Module module;

// COMENTADO POR SER SUSPEITO DE CAUSA DE PROBLEMAS DE OBJETOS TRANSIENTS NO HIBERNATE
//    @ManyToMany
//    @JoinTable(name="admin_profile_role",
//				joinColumns=@JoinColumn(name="roleId"),
//				inverseJoinColumns=@JoinColumn(name="profileId"))
//     private Collection<Profile> profiles;

//	public Collection<Profile> getProfiles() {
//		return profiles;
//	}

//	public void setProfiles(Collection<Profile> profiles) {
//		this.profiles = profiles;
//	}

	public Role() {
	}

	public Role(String roleId) {
		this.roleId = roleId;
	}


	public Role(String roleId, String title) {
		this.title = title;
	}
    
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}
    
    @Override
    public String toString() {
        return this.title;
    }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof DefaultRole && ((DefaultRole)obj).getRoleId().equals(this.getRoleId());
	}
	
}
