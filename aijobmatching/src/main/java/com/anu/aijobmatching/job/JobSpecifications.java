package com.anu.aijobmatching.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

public class JobSpecifications {
    public static Specification<Job> ownerEqualsEmail(String owneremail) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(criteriaBuilder.lower(root.get("owner").get("email")), "%" + owneremail.toLowerCase() + "%");
    }

    public static Specification<Job> companyContainsIgnoreCase(String company) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(criteriaBuilder.lower(root.get("company")), "%" + company.toLowerCase() + "%");
    }

    public static Specification<Job> qContainsIgnoreCase(String q) {
        return (root, query, criteriaBuilder) -> {
            String like = "%" + q.toLowerCase() + "%";
            List<Predicate> or = new ArrayList<>();
            or.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), like));
            or.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), like));
            or.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("requirements")), like));
            return criteriaBuilder.or(or.toArray(new Predicate[0]));
        };
    }


    public static Specification<Job> skillsAnyContainsIgnoreCase(Set<String> skills) {
        return (root, query, criteriaBuilder) -> {
            if(skills == null || skills.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> ors = new ArrayList<>();
            for(String skill : skills) {
                String like = "%" + skill.toLowerCase() + "%";    
                ors.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("skillsCV")), like));
                ors.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("requirements")), like));
                ors.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), like));
                ors.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), like));
            }
            return criteriaBuilder.or(ors.toArray(new Predicate[0]));
        };
    }
}