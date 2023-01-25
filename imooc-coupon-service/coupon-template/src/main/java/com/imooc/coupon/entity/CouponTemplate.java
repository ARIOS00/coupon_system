package com.imooc.coupon.entity;

// coupon template entity class definition

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
public class CouponTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String desc;

    @Column(name = "category", nullable = false)
    private CouponCategory category;

    @Column(name = "product_line", nullable = false)
    private ProductLine productLine;

    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @Column(name = "create_time", nullable = false)
    @CreatedDate
    private Date createTime;
    // the user who created this
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;
    // target users
    @Column(name = "target", nullable = false)
    private DistributeTarget target;

    @Column(name = "rule", nullable = false)
    private TemplateRule rule;

    public CouponTemplate(String name, String logo, String desc, String category,
                          Integer productLine, Integer count, Long userId,
                          Integer target, TemplateRule rule) {
        this.available = false;
        this.expired = false;
        this.logo = logo;
        this.name = name;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        // coupon key = 4 digits product line and type + 8 digits date + 4 digits id
        this.key = productLine.toString() + category + new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }
}
