/**
 * Copyright (C) 2012 IP SQUARE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ipsquare.commons.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

import at.ipsquare.commons.interfaces.AbstractUnitOfWork;
import at.ipsquare.commons.interfaces.UnitOfWork;

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
    
    private static class SomeRepo1 extends DefaultHibernateRepository
    {
        public SomeRepo1(HibernateConfiguration hibernateCfg)
        {
            super(hibernateCfg);
        }
    }
    
    private static class SomeRepo2 extends DefaultHibernateRepository
    {
        public SomeRepo2(HibernateConfiguration hibernateCfg)
        {
            super(hibernateCfg);
        }
    }
    
    /**
     * Assert that we fail fast if an unfortunate user attempts to create multiple repository instances of the same type.
     */
    @Test
    public void verifyThatMultipleInstancesOfTheSameRepoClassLeadToAnException()
    {
        HibernateConfiguration cfg = new UnitTestHibernateConfiguration();
        @SuppressWarnings("resource")
        SomeRepo1 repo1 = new SomeRepo1(cfg);
        @SuppressWarnings("resource")
        SomeRepo2 repo2 = new SomeRepo2(cfg);
        
        try
        {
            new SomeRepo1(cfg);
            fail();
        }
        catch(IllegalStateException e)
        {
            // OK!
        }
        
        try
        {
            new SomeRepo2(cfg);
            fail();
        }
        catch(IllegalStateException e)
        {
            // OK!
        }
        
        repo1.close();
        repo2.close();
        
        repo1 = new SomeRepo1(cfg);
        repo2 = new SomeRepo2(cfg);
    }
}
