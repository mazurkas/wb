package com.whosenet.wb.bbs.entity;

import com.whosenet.wb.core.entity.OrderEntity;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 分类
 *
 * @author <a href="http://ruoo.whosenet.com">ruoo.whosenet.com</>
 * @version 1.0
 */
@Entity
@Table(name = "wb_bbs_category")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "wb_bbs_category_sequence")
public class Category extends OrderEntity {

    /** 分类名称 */
    private String name;


    @NotEmpty
    @Length(max = 200)
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
