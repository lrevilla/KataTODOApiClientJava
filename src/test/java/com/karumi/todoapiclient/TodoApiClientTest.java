/*
 *   Copyright (C) 2016 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.todoapiclient;

import com.karumi.todoapiclient.dto.TaskDto;
import com.karumi.todoapiclient.exception.ItemNotFoundException;
import com.karumi.todoapiclient.exception.TodoApiClientException;
import com.karumi.todoapiclient.exception.UnknownErrorException;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TodoApiClientTest extends MockWebServerTest {

  private TodoApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    String mockWebServerEndpoint = getBaseEndpoint();
    apiClient = new TodoApiClient(mockWebServerEndpoint);
  }

  @Test
  public void shouldReturnTheTasksWhenTheResponseIsParsed() throws Exception {
    enqueueMockResponse(200, "getTasksResponse.json");

    List<TaskDto> tasks = apiClient.getAllTasks();

    assertEquals(200, tasks.size());
    assertTaskContainsExpectedValues(tasks.get(0));
  }

  @Test
  public void shouldReturnTheTasksWhenThePathAndVerbAreValid() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertGetRequestSentTo("/todos");
  }

  @Test(expected = UnknownErrorException.class)
  public void shouldFailWhenWhenServerReturnsFailure() throws Exception {
    enqueueMockResponse(418);

    apiClient.getAllTasks();
  }

  @Test
  public void shouldCallValidPathWhenGettingDetails() throws Exception {
    enqueueMockResponse();

    apiClient.getTaskById("1");

    assertGetRequestSentTo("/todos/1");
  }

  @Test(expected = ItemNotFoundException.class)
  public void shouldFailWhenCallingNonExistingPath() throws Exception {
    enqueueMockResponse(404);

    apiClient.getTaskById("1000");
  }

  @Test(expected = TodoApiClientException.class)
  public void shouldFailWhenServerFails() throws Exception {
    enqueueMockResponse(500);

    apiClient.getTaskById("1000");
  }

  @Test
  public void shouldSuccessWhenResponseIsValid() throws Exception {
    enqueueMockResponse(200, "getTaskByIdResponse.json");

    TaskDto taskResponse = apiClient.getTaskById("1");

    assertTaskContainsExpectedValues(taskResponse);
  }

  private void assertTaskContainsExpectedValues(TaskDto task) {
    assertEquals(task.getId(), "1");
    assertEquals(task.getUserId(), "1");
    assertEquals(task.getTitle(), "delectus aut autem");
    assertFalse(task.isFinished());
  }
}
