package com.ec.survey.model;

import com.ec.survey.model.administration.User;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.Tools;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "WHITELISTEDUSERS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WhitelistedUser implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
    private User user;
	private int surveys;
	private Date added;
    private Set<WhitelistLabel> labels = new HashSet<>();

    @Id
    @Column(name = "WHITELISTEDUSER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "WHITELISTEDUSER_USER", nullable = false)
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public String getName() {
        return user.getFirstLastName();
    }

    @Transient
    public String getEmail() {
        return user.getEmail();
    }

    @ManyToMany(targetEntity = WhitelistLabel.class)
    @JoinTable(foreignKey = @ForeignKey(javax.persistence.ConstraintMode.NO_CONSTRAINT),
            joinColumns = @JoinColumn(name = "WHITELISTEDUSER_ID"),
            inverseJoinColumns = @JoinColumn(name = "LABEL_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    public Set<WhitelistLabel> getLabels() {
        return labels;
    }

    public void setLabels(Set<WhitelistLabel> labels) {
        this.labels = labels;
    }

    @Transient
    public int getSurveys() {
        return surveys;
    }

    public void setSurveys(int surveys) {
        this.surveys = surveys;
    }

    @Column(name = "WHITELISTEDUSER_ADDED")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = ConversionTools.DateTimeFormatSmall)
    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    @Transient
    public String getAddedString() {
        return Tools.formatDate(added, ConversionTools.DateFormat);
    }

    @Transient
    public WhitelistLabel[] getLabelsSorted() {
        ArrayList<WhitelistLabel> list = new ArrayList<>(this.labels);
        list.sort(Comparator.comparing(WhitelistLabel::getName));
        return list.toArray(new WhitelistLabel[0]);
    }

}
