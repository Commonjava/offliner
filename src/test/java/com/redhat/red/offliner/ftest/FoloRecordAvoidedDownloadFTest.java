/*
 * Copyright (C) 2015 Red Hat, Inc.
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
package com.redhat.red.offliner.ftest;

import com.redhat.red.offliner.Offliner;
import com.redhat.red.offliner.OfflinerResult;
import com.redhat.red.offliner.cli.Options;
import com.redhat.red.offliner.ftest.fixture.TestRepositoryServer;
import org.commonjava.indy.folo.dto.TrackedContentDTO;
import org.commonjava.indy.folo.dto.TrackedContentEntryDTO;
import org.commonjava.indy.folo.model.TrackingKey;
import org.commonjava.indy.model.core.StoreKey;
import org.commonjava.indy.model.core.StoreType;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test the avoided result of re-download existing files.
 */
public class FoloRecordAvoidedDownloadFTest
        extends AbstractOfflinerFunctionalTest
{
    /**
     * In general, we should only have one test method per functional test. This allows for the best parallelism when we
     * execute the tests, especially if the setup takes some time.
     *
     * @throws Exception In case anything (anything at all) goes wrong!
     */
    @Test
    public void run()
            throws Exception
    {
        // We only need one repo server.
        TestRepositoryServer server = newRepositoryServer();

        // Generate some test content
        byte[] content = contentGenerator.newBinaryContent( 1024 );

        TrackedContentEntryDTO dto =
                contentGenerator.newRemoteContentEntry( new StoreKey( StoreType.remote, "test" ), "jar",
                                                        server.getBaseUri(), content );

        TrackedContentDTO record = new TrackedContentDTO( new TrackingKey( "test-record" ), Collections.emptySet(),
                                                          Collections.singleton( dto ) );

        String path = dto.getPath();

        // Register the generated content by writing it to the path within the repo server's dir structure.
        // This way when the path is requested it can be downloaded instead of returning a 404.
        server.registerContent( path, content );
        server.registerContent( path + Offliner.SHA_SUFFIX, sha1Hex( content ) );
        server.registerContent( path + Offliner.MD5_SUFFIX, md5Hex( content ) );

        // Write the plaintext file we'll use as input.
        File foloRecord = temporaryFolder.newFile( "folo." + getClass().getSimpleName() + ".json" );

        FileUtils.write( foloRecord, objectMapper.writeValueAsString( record ) );

        Options opts = new Options();
        opts.setBaseUrls( Collections.singletonList( server.getBaseUri() ) );

        // Capture the downloads here so we can verify the content.
        File downloads = temporaryFolder.newFolder();

        opts.setDownloads( downloads );
        opts.setLocations( Collections.singletonList( foloRecord.getAbsolutePath() ) );

        OfflinerResult firstMain = run( opts );
        assertThat( "Wrong number of downloads logged. Should have been 3 with checksum files included.", firstMain.getDownloaded(), equalTo( 3 ) );

        //re-run to test the function of avoiding re-downloading existing files
        OfflinerResult secondMain = run( opts );
        assertThat( "Wrong number of downloads logged. Should have been 0.", secondMain.getDownloaded(), equalTo( 0 ) );
        assertThat( "Wrong number of avoided downloads logged. Should have been 1", secondMain.getAvoided(),
                    equalTo( 3 ) );
        assertThat( "Errors should be empty!", secondMain.getErrors().isEmpty(), equalTo( true ) );
    }
}
