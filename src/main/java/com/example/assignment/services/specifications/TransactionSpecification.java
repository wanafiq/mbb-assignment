package com.example.assignment.services.specifications;

import com.example.assignment.domains.Transaction;
import com.example.assignment.web.dto.TransactionSearchDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> withFilters(TransactionSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDTO.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), searchDTO.getCustomerId()));
            }

            if (searchDTO.getAccountNo() != null && !searchDTO.getAccountNo().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("accountNo"), searchDTO.getAccountNo()));
            }

            if (searchDTO.getDescription() != null && !searchDTO.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), 
                    "%" + searchDTO.getDescription().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}