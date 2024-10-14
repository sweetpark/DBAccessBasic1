package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */

@Slf4j

public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;

    private final PlatformTransactionManager transactionManager;
    //private final DataSource dataSource;

    @Autowired
    public MemberServiceV3_1(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager){
        this.memberRepository = memberRepository;
        this.transactionManager = transactionManager;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException{

        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            //비지니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);


        } catch (Exception e){
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }



    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
        memberRepository.update(toId, toMember.getMoney() + money);
    }
}
