package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.entity.Accounts;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AccountDAO {
    @PersistenceContext
    private EntityManager em;

    public Long save(Accounts member){
        em.persist(member);
        return member.getAccountId();
    }

    public Accounts find(Long id){
        return em.find(Accounts.class, id);
    }

}
