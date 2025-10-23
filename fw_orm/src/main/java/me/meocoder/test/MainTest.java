package me.meocoder.test;

import me.meocoder.anotation.Transactional;
import me.meocoder.config.SimpleDataSource;
import me.meocoder.processor.EntityManager;
import me.meocoder.processor.RepositoryProxy;
import me.meocoder.processor.TransactionManager;
import me.meocoder.processor.TransactionalProxy;
import me.meocoder.test.service.IUserService;
import me.meocoder.test.service.UserRepository;
import me.meocoder.test.service.UserServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MainTest {
    public static void main(String[] args) throws SQLException {
        // init datasource + tx + em
        SimpleDataSource ds = new SimpleDataSource("jdbc:h2:mem:miniorm;DB_CLOSE_DELAY=-1", "sa", "");
        TransactionManager txManager = new TransactionManager(ds);
        EntityManager em = new EntityManager(ds, txManager);

        // create table
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))");
        }

        // create repository proxy
        UserRepository userRepo = RepositoryProxy.create(UserRepository.class, em);

        // create service and wrap with transactional proxy
        UserServiceImpl svcImpl = new UserServiceImpl(userRepo, em);
        IUserService svc = TransactionalProxy.createProxy(svcImpl, txManager);

        // normal operation: create users inside transactions
        svc.createUser("Nguyen");
        svc.createUser("Linh");

        // read back
        Users u1 = svc.getUser(1L);
        System.out.println("User1 = " + u1);

        List<Users> all = svc.listAll();
        System.out.println("All = " + all);

        // test rollback: make service throw runtime exception after saving -> should rollback
        try {
            IUserService badSvc = TransactionalProxy.createProxy(new IUserService() {
                @Override
                @Transactional
                public void createUser(String name) {
                    // use em directly to demonstrate tx across em + repo
                    Users u = new Users(name);
                    em.persist(u); // this will use tx connection
                    System.out.println("about to throw...");
                    throw new RuntimeException("boom");
                }
                @Override public Users getUser(Long id) { return null; }
                @Override public List<Users> listAll() { return null; }
            }, txManager);

            try {
                badSvc.createUser("ShouldRollback");
            } catch (RuntimeException ex) {
                System.out.println("caught exception, transaction rolled back");
            }

            // verify no inserted row
            List<Users> after = em.queryList("SELECT * FROM users", Users.class);
            System.out.println("After rollback, all = " + after);
        } finally {
            // done
        }
    }
}
