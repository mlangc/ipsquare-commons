package at.ipsquare.hibernate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

/**
 * Tests for {@link DefaultHibernateRepository}.
 * 
 * @author Matthias Langer
 */
public class TestDefaultHibernateRepository
{
    private final HibernateRepository repo = new DefaultHibernateRepository(new UnitTestHibernateConfiguration());
    
    /**
     * Tests {@link DefaultHibernateRepository#executeUnitOfWork(UnitOfWork)}.
     */
    @Test(invocationCount = 1, threadPoolSize = 4, timeOut = 5000L)
    public void testExecuteDbOperations()
    {
        // Write a few object to the DB:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                UnitTestEntityParent parent = new UnitTestEntityParent(threadLocalName("parent"));
                
                UnitTestEntityChild child1 = new UnitTestEntityChild(threadLocalName("child"));
                child1.setParent(parent);
                
                UnitTestEntityChild child2 = new UnitTestEntityChild(threadLocalName("child2"));
                child2.setParent(parent);
                
                parent.getChildren().add(child1);
                parent.getChildren().add(child2);
                
                repo.currentSession().saveOrUpdate(parent);
                return null;
            }
        });
        
        // Check that the DB contains the objects we just stored there:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                UnitTestEntityParent parent = parentWithName("parent");
                
                assertNotNull(parent);
                assertNotNull(parent.getChildren());
                assertEquals(2, parent.getChildren().size());
                return null;
            }
        });
        
        // Persist some crap and rollback:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                UnitTestEntityParent parent = new UnitTestEntityParent(threadLocalName("evil"));
                repo.currentSession().saveOrUpdate(parent);
                repo.currentSession().getTransaction().rollback();
                return null;
            }
        });
        
        // Make sure the rollback worked:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                UnitTestEntityParent parent = parentWithName("evil");
                assertNull(parent);
                return null;
            }
        });
        
        // Explicitly start a new transaction:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                repo.currentSession().getTransaction().commit();
                repo.currentSession().beginTransaction();
                
                UnitTestEntityParent parent = new UnitTestEntityParent(threadLocalName("parent2"));
                repo.currentSession().saveOrUpdate(parent);
                return null;
            }
        });
        
        // Make sure that our new parent has been persisted:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                assertNotNull(parentWithName("parent2"));
                return null;
            }
        });
        
        // Make sure that starting a UnitOfWork within a UnitOfWork works:
        repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
        {
            public Void execute() throws Exception
            {
                return repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
                {
                    public Void execute() throws Exception
                    {
                        assertNotNull(parentWithName("parent"));
                        return null;
                    }
                });
            }
        });
    }
    
    /**
     * Verifies that {@link DefaultHibernateRepository#currentSession()} behaves properly without a {@link UnitOfWork} available.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testCurrentSessionWithoutUnitOfWork()
    {
        repo.currentSession();
    }

    /**
     * Tests {@link DefaultHibernateRepository#executeUnitOfWork(UnitOfWork)}.
     */
    @Test
    public void testExectuteExceptionPassThrough()
    {
        try
        {
            repo.executeUnitOfWork(new AbstractUnitOfWork<Void>()
            {
                public Void execute() throws Exception
                {
                    throw new Exception("Ups!");
                }
            });
            fail("Exception expected!");
        }
        catch(ExecutionError e)
        {
            assertNotNull(e.getCause());
            assertEquals("Ups!", e.getCause().getMessage());
        }
    }
    
    private static String threadLocalName(String name)
    {
        return name + "_" + Thread.currentThread().getId();
    }

    /**
     * Tests {@link DefaultHibernateRepository#executeUnitOfWork(UnitOfWork)}.
     */
    @Test
    public void testExecuteReturnValuePassThrough()
    {
        assertEquals(Integer.valueOf(0), repo.executeUnitOfWork(new AbstractUnitOfWork<Integer>()
        {
            public Integer execute() throws Exception
            {
                return 0;
            }
        }));
    }
    
    private UnitTestEntityParent parentWithName(String name)
    {
        Criteria c = repo.currentSession().createCriteria(UnitTestEntityParent.class)
                         .add(Restrictions.eq("name", threadLocalName(name)));
        return (UnitTestEntityParent) c.uniqueResult();
    }
}
