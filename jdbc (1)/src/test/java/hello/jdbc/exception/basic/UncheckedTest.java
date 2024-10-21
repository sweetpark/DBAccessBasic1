package hello.jdbc.exception.basic;

import com.jayway.jsonpath.internal.path.PredicatePathToken;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void callCatch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void callThrow(){
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyUnCheckedException.class);
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다
     */
    static class MyUnCheckedException extends RuntimeException{
        public MyUnCheckedException(String message) {
            super(message);
        }
    }


    /**
     * Unchecked 에외는
     * 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다
     */
    static class Service{
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch(){

            try{
                repository.call();
            }catch (MyUnCheckedException e){
                log.info("예외처리, message={}", e.getMessage(), e);
            }

        }

        public void callThrow(){
            repository.call();
        }

    }


    static class Repository{
        public void call(){
            throw new MyUnCheckedException("ex");
        }
    }


}
