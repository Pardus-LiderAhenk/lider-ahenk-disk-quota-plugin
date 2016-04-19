package tr.org.liderahenk.disk.quota.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "P_DiskQuota")
public class DiskQuotaEntity {
	
	// This class is auto-generated.
	// Please follow the (table and column) naming conventions.
	
	@Id
	@GeneratedValue
	@Column(name = "${entity}_ID")
	private Long id;
	
	// Other database columns...
	
	// Getter setters
	
}
