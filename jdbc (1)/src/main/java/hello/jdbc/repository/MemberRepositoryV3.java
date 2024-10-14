package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/***
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection();
 * DataSourceUtils.releaseConnection();
 */

@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    //DataSource 의존 (주입 필요)
    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            //pstmt ==> sql "?" 바인딩하는 값
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally{
            //무조건 con 종료 해줘야함 (외부 리소스 사용중)
            close(con, pstmt, null);
        }

    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            //조회 결과
            rs = pstmt.executeQuery();
            if (rs.next()){ // 처음에는 아무것도 안가리키다, next()호출하면 첫번째부터 순차적으로
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.info("error",e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }

    }


    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            log.info("error",e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        //커넥션 풀 반환
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
        DataSourceUtils.releaseConnection(con, dataSource);

    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야함
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}",con);

        return con;
    }
}
