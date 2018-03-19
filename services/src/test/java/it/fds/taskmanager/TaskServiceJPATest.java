package it.fds.taskmanager;

import it.fds.taskmanager.dto.TaskDTO;
import it.fds.taskmanager.model.Task;
import it.fds.taskmanager.repository.TasksRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Basic test suite to test the service layer, it uses an in-memory H2 database. 
 * 
 * @author Carolyn Hung 2018.
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 10 Jan. 2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TaskServiceJPATest extends Assert{

    @InjectMocks
    TaskServiceJPA taskServiceJPA;

    @Mock
    private TasksRepository tasksRepositoryMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void writeAndReadOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Test task1");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO t1 = taskServiceJPA.saveTask(t);
        TaskDTO tOut = taskServiceJPA.findOne(t1.getUuid());
        assertEquals("Test task1", tOut.getTitle());
        List<TaskDTO> list = taskServiceJPA.showList();
        assertEquals(1, list.size());
    }

    @Test
    public void showListReturnsListOfTasksSuccessfully() {
        // Arrange
        final List<Task> expectedList = new ArrayList<>();
        final Task task1 = new Task();
        task1.setUuid(UUID.randomUUID());
        task1.setDescription("Task1");
        expectedList.add(task1);
        when(tasksRepositoryMock.findAllExcludePostponed()).thenReturn(expectedList);

        // Act
        final List<TaskDTO> actualList = taskServiceJPA.showList();

        // Assert
        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertSame(expectedList.get(0).getDescription(), actualList.get(0).getDescription());

    }

    @Test
    public void findOneTaskByUuidReturnsTaskSuccessfully() {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        final Task task = new Task();
        task.setUuid(uuid);
        task.setDescription("Description");
        when(tasksRepositoryMock.findByUuid(uuid)).thenReturn(task);

        // Act
        final TaskDTO actualTaskDto = taskServiceJPA.findOne(uuid);

        // Assert
        assertNotNull(actualTaskDto);
        assertEquals(actualTaskDto.getDescription(), task.getDescription());
        assertSame(actualTaskDto.getUuid(), task.getUuid());
    }

    @Test
    public void saveTaskSuccessfully() {

        // Arrange
        final TaskDTO task = new TaskDTO();
        task.setStatus(TaskState.NEW.name().toString());
        task.setDescription("test description");
        task.setTitle("test title");
        final Task newTaskMock = new Task();
        newTaskMock.setDescription("test description");
        newTaskMock.setTitle("test title");
        newTaskMock.setStatus(TaskState.NEW.name().toString());
        when(tasksRepositoryMock.save(any(Task.class))).thenReturn(newTaskMock);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        // Act
        final TaskDTO actualTaskDto = taskServiceJPA.saveTask(task);

        // Assert
        verify(tasksRepositoryMock, times(1)).save(taskCaptor.capture());
        assertNotNull(actualTaskDto);
        Task taskCaptured = taskCaptor.getValue();
        assertNotNull(taskCaptured);
        assertSame(actualTaskDto.getUuid(), taskCaptured.getUuid().toString()); //actualTaskDto's uuid is not copied over
    }

    @Test
    public void updateTaskSuccessfully() {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        final TaskDTO task = new TaskDTO();
        task.setUuid(uuid);
        task.setDescription("test description");
        task.setTitle("test title");
        task.setStatus(TaskState.NEW.name().toString());
        task.setUpdatedat(null);
        final Task newTask = new Task();
        newTask.setUuid(uuid);
        newTask.setDescription("test description");
        newTask.setTitle("test title");
        newTask.setStatus(TaskState.NEW.name().toString());
        when(tasksRepositoryMock.save(isA(Task.class))).thenReturn(newTask);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        // Act
        final TaskDTO actualTaskDto = taskServiceJPA.updateTask(task);

        // Assert
        verify(tasksRepositoryMock, times(1)).save(taskCaptor.capture());
        assertNotNull(actualTaskDto);
        Task taskCaptured = taskCaptor.getValue();
        assertNotNull(taskCaptured);
        assertNotNull(taskCaptured.getUpdatedat());
        assertEquals("test description", actualTaskDto.getDescription());
        assertEquals("test title", actualTaskDto.getTitle());
        assertNotNull(actualTaskDto.getUuid());
    }


    @Test
    public void resolveTaskReturnsTrueWhenTaskIsResolved(){
        // Arrange
        final Task task = new Task();
        final UUID uuid = UUID.randomUUID();
        task.setUuid(uuid);
        task.setTitle("test title");
        task.setDescription("test description");

        when(tasksRepositoryMock.findByUuid(uuid)).thenReturn(task);

        // Act
        final Boolean actualIsResolved = taskServiceJPA.resolveTask(uuid);

        // Assert
        assertTrue(actualIsResolved);
    }

    @Test
    public void postponeTaskReturnsTrueWhenTimeIsAddedSuccessfully() {
        // Arrange
        final Task task = new Task();
        final Integer timeInMinute = 25;
        final UUID uuid = UUID.randomUUID();
        task.setUuid(uuid);
        task.setTitle("test title");
        task.setDescription("test description");

        when(tasksRepositoryMock.findByUuid(uuid)).thenReturn(task);

        // Act
        final Boolean actualIsPostponed = taskServiceJPA.postponeTask(uuid, timeInMinute);

        // Assert
        assertTrue(actualIsPostponed);
    }

    @Test
    public void unmarkPostponedTaskReturnsStateRestoredSuccessfully() {
        // Arrange

        final Task task1 = new Task();
        final UUID uuid1 = UUID.randomUUID();
        task1.setUuid(uuid1);
        task1.setTitle("task1 title");
        task1.setDescription("task1 description");
        task1.setPostponedat(Calendar.getInstance());
        task1.setStatus(TaskState.POSTPONED.name().toString());

        final Task task2 = new Task();
        final UUID uuid2 = UUID.randomUUID();
        task2.setUuid(uuid2);
        task2.setTitle("task2 title");
        task2.setDescription("task2 description");
        task2.setPostponedat(Calendar.getInstance());
        task2.setStatus(TaskState.POSTPONED.name().toString());

        final List<Task> taskList = Arrays.asList(task1, task2);
        when(tasksRepositoryMock.findTaskToRestore()).thenReturn(taskList);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        // Act
        taskServiceJPA.unmarkPostoned();

        // Assert
        verify(tasksRepositoryMock, times(2)).save(taskCaptor.capture());
        final List<Task> actualTaskList = taskCaptor.getAllValues();
        assertNotNull(actualTaskList);
        assertEquals(2, actualTaskList.size());
        for (Task task : actualTaskList) {
            assertEquals("RESTORED", task.getStatus());
            assertNull(task.getPostponedat());
        }

    }

    @EnableJpaRepositories
    @Configuration
    @SpringBootApplication
    public static class EndpointsMain{}
}
