package com.gc.projeto.modules.companies.infrastructure.specifications;

import com.gc.projeto.modules.companies.infrastructure.CompanyEntity;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpecifications {

    public static Specification<CompanyEntity> nameContainsIgnoreCase(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<CompanyEntity> isResidentEquals(Boolean isResident) {
        return (root, query, cb) -> {
            if (isResident == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isResident"), isResident);
        };
    }
}
