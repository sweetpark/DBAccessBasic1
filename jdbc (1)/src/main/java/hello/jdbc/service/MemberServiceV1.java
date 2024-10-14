package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV0;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    @Autowired
    public MemberServiceV1(MemberRepositoryV1 memberRepository){
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException{
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
        memberRepository.update(toId, toMember.getMoney() + money);


    }
}
