package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */

@Slf4j

public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    @Autowired
    public MemberServiceV2(MemberRepositoryV2 memberRepository, DataSource dataSource){
        this.memberRepository = memberRepository;
        this.dataSource = dataSource;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException{

        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false);

            //비지니스 로직
            bizLogic(fromId, toId, money, con);


            con.commit(); // 성공시 commit

        } catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            //JdbcUtils.closeConnection(con);
            if (con != null){
                try{
                    con.setAutoCommit(true); // 자동 commit 모드로 변경 (디폴트값)
                    con.close(); // 커넥션 풀로 반환
                }catch (Exception e){
                    log.info("error", e);
                }
            }
        }



    }

    private void bizLogic(String fromId, String toId, int money, Connection con) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con,fromId, fromMember.getMoney() - money);
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
        memberRepository.update(con,toId, toMember.getMoney() + money);
    }
}
