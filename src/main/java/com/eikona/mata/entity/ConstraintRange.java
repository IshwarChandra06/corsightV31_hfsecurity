package com.eikona.mata.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="constraint_range")
public class ConstraintRange implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private Long id;

    @Column(name = "min_val")
    @NotBlank(message="Please provide a valid value.")
    private String min_value;
    
    @Column(name = "max_val")
    @NotBlank(message="Please provide a valid value.")
    private String max_value;
    
    @NotBlank(message="Please provide type")
    @Column(name = "type", unique = true, nullable = false)
    private String type;
    
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMin_value() {
		return min_value;
	}


	public void setMin_value(String min_value) {
		this.min_value = min_value;
	}


	public String getMax_value() {
		return max_value;
	}


	public void setMax_value(String max_value) {
		this.max_value = max_value;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	public ConstraintRange() {
		super();
	}

	public ConstraintRange(@NotBlank(message = "Please provide a valid value.") String min_value,
			@NotBlank(message = "Please provide a valid value.") String max_value,
			@NotBlank(message = "Please provide type") String type, boolean isDeleted) {
		super();
		this.min_value = min_value;
		this.max_value = max_value;
		this.type = type;
		this.isDeleted = isDeleted;
	}
    
    

}
