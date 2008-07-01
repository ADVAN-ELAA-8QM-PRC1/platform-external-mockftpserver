/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mockftpserver.fake.command

import org.mockftpserver.core.command.Command
import org.mockftpserver.core.command.CommandHandler
import org.mockftpserver.core.command.CommandNames
import org.mockftpserver.core.command.ReplyCodes
import org.mockftpserver.core.session.SessionKeys

/**
 * Tests for MkdCommandHandler
 *
 * @version $Revision$ - $Date$
 *
 * @author Chris Mair
 */
class MkdCommandHandlerTest extends AbstractLoginRequiredCommandHandlerTest {

    def DIRNAME = "usr"
    def DIR = p('/', DIRNAME)

    void testHandleCommand() {
        commandHandler.handleCommand(createCommand([DIR]), session)
        assertSessionReply(ReplyCodes.MKD_OK)
        assert fileSystem.exists(DIR)
    }

    void testHandleCommand_PathIsRelative() {
        session.setAttribute(SessionKeys.CURRENT_DIRECTORY, '/')
        commandHandler.handleCommand(createCommand([DIRNAME]), session)
        assertSessionReply(ReplyCodes.MKD_OK)
        assert fileSystem.exists(DIR)
    }

    void testHandleCommand_ParentDirectoryDoesNotExist() {
        commandHandler.handleCommand(createCommand(['/abc/def']), session)
        assertSessionReply(ReplyCodes.EXISTING_FILE_ERROR, '/abc')
    }

    void testHandleCommand_PathSpecifiesAFile() {
        assert fileSystem.createFile(DIR)
        commandHandler.handleCommand(createCommand([DIR]), session)
        assertSessionReply(ReplyCodes.EXISTING_FILE_ERROR, DIR)
        assert fileSystem.exists(DIR)
    }

    void testHandleCommand_MissingPathParameter() {
        testHandleCommand_MissingRequiredParameter([])
    }

    void testHandleCommand_CreateDirectoryThrowsException() {
        overrideMethodToThrowFileSystemException("createDirectory")
        handleCommand([DIR])
        assertSessionReply(ReplyCodes.EXISTING_FILE_ERROR)
    }

    void setUp() {
        super.setUp()
        fileSystem.createDirectory('/')
    }

    //-------------------------------------------------------------------------
    // Helper Methods
    //-------------------------------------------------------------------------

    CommandHandler createCommandHandler() {
        new MkdCommandHandler()
    }

    Command createValidCommand() {
        return new Command(CommandNames.MKD, [DIR])
    }

}