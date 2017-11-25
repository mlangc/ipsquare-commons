/**
 * Copyright (C) 2017 Matthias Langer
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
package at.ipsquare.commons.core.util;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.is;

public class TestPerformanceLoggerBlockBasedApi
{
	@Test
	public void testTimedRunables()
	{
		String funName = "testTimedRunables";
		assertThat(UnitTestAppender.logString(), not(containsString(funName)));

		boolean[] blockExecuted = { false };
		PerformanceLogger.timedExec(() -> {
			blockExecuted[0] = true;
		});
		
		assertTrue(blockExecuted[0]);
		assertThat(UnitTestAppender.logString(), containsString(funName));

		blockExecuted[0] = false;
		String marker = funName + ":marker";

		PerformanceLogger.timedExec(1000, marker, () -> {
			blockExecuted[0] = true;
		});

		assertTrue(blockExecuted[0]);
		assertThat(UnitTestAppender.logString(), not(containsString(marker)));

		blockExecuted[0] = false;

		PerformanceLogger.timedExec(marker, () -> {
			blockExecuted[0] = true;
		});

		assertTrue(blockExecuted[0]);
		assertThat(UnitTestAppender.logString(),containsString(marker));
	}
	
	@Test
	public void testTimedProviders() {
		String funName = "testTimedProviders";
		String expectedResult = "42";
		assertThat(UnitTestAppender.logString(), not(containsString(funName)));
		
		String actualResult = PerformanceLogger.timedExec(() -> expectedResult);
		assertThat(expectedResult, is(actualResult));
		assertThat(UnitTestAppender.logString(), containsString(funName));
		
		String marker = funName + ":marker";
		
		actualResult = PerformanceLogger.timedExec(1000, marker, () -> expectedResult);
		assertThat(expectedResult, is(actualResult));
		assertThat(UnitTestAppender.logString(), not(containsString(marker)));

		PerformanceLogger.timedExec(marker, () -> expectedResult);
	}
}
