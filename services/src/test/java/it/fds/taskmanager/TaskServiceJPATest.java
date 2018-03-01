package it.fds.taskmanager;

import it.fds.taskmanager.dto.TaskDTO;
import it.fds.taskmanager.model.Task;
import it.fds.taskmanager.repository.TasksRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * Basic test suite to test the service layer, it uses an in-memory H2 database. 
 * 
 * TODO Add more and meaningful tests! :)
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
    public void saveTaskStoresTaskSuccessfully() {
        // Arrange
        final UUID uuid = UUID.randomUUID();
        final TaskDTO taskDto = new TaskDTO();
        final Task newTask = new Task();
        newTask.setUuid(uuid);
        newTask.setDescription("Test One");
        taskDto.setDescription("Test One");
        taskDto.setUuid(uuid);
        taskDto.setPriority("High");
        when(tasksRepositoryMock.save(isA(Task.class))).thenReturn(newTask);

        // Act
        final TaskDTO actualTaskDto = taskServiceJPA.saveTask(taskDto);

        // Assert
        assertNotNull(actualTaskDto);
        assertSame(actualTaskDto.getUuid(), taskDto.getUuid());
        assertEquals(actualTaskDto.getDescription(), taskDto.getDescription());
    }

    @Test
    public void updateTaskReturnsUpdatedTaskSuccessfully() {
        // Arrange
        final TaskDTO taskDTO = new TaskDTO();
        final Task newTask = new Task();
        final UUID uuid = UUID.randomUUID();
        taskDTO.setUuid(uuid);
        taskDTO.setDescription("Old description");
        taskDTO.setCreatedat(Calendar.getInstance());
        when(tasksRepositoryMock.save(isA(Task.class))).thenReturn(newTask);

        // Act
        final TaskDTO actualTaskDto = taskServiceJPA.updateTask(taskDTO);

        // Assert
        assertNotNull(actualTaskDto);
    }

    @EnableJpaRepositories
    @Configuration
    @SpringBootApplication
    public static class EndpointsMain{}
}
