package com.insa.TeamOpsSystem.sixmonthchekelist;
import com.insa.TeamOpsSystem.sites.Sites;
import com.insa.TeamOpsSystem.until.Auditable;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "sixmclist")
@Data
@Where(clause = "deleted=0")
@SQLDelete(sql = "UPDATE sixmclist SET deleted = 1 WHERE id=? and version=?")
public class SixMCList extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private  String date;
    @Column(nullable = false)
    private  String datacenter;
    @Column(nullable = false)
    private  String fiber;
    @Column(nullable = false)
    private String rack;
    @Column(nullable = false)
    private String opd;
    private String Switch;
    private String t9140;
    private String server;
    private String routine;
    @ManyToOne
    private Sites sites;
}