package org.openchs.service;

import org.openchs.dao.IndividualRepository;
import org.openchs.domain.Encounter;
import org.openchs.domain.Individual;
import org.openchs.web.request.EncounterContract;
import org.openchs.web.request.IndividualContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndividualSearchService {
    private final IndividualRepository individualRepository;
    private final EntityManager entityManager;

@Autowired
    public IndividualSearchService(IndividualRepository individualRepository, EntityManagerFactory entityManagerFactory)
{
    this.individualRepository = individualRepository;
    this.entityManager = entityManagerFactory.createEntityManager();
}

    public List<IndividualContract> getsearch(String jsonSearch) {
        Query q = entityManager.createNativeQuery("select firstname,lastname,id,uuid,title_lineage " +
                "from search_function_2 (?1)");
        q.setParameter(1, jsonSearch);
        List<Object[]> obj = q.getResultList();
        return constructIndividual(obj);
    }

    private List<IndividualContract> constructIndividual(List<Object[]> individualList) {
        return individualList.stream()
            .map(individualRecord -> {
                IndividualContract individualContract = new IndividualContract();
                individualContract.setFirstName((String) individualRecord[0]);
                individualContract.setLastName((String) individualRecord[1]);
                individualContract.setId(new Long(individualRecord[2].toString()));
                individualContract.setUuid((String) individualRecord[3]);
                individualContract.setAddressLevel((String) individualRecord[4]);
                return individualContract;
            }).collect(Collectors.toList());
    }
}
