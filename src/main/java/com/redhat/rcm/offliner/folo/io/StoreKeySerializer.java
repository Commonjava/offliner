/**
 * Copyright (C) 2011 Red Hat, Inc. (jdcasey@commonjava.org)
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
package com.redhat.rcm.offliner.folo.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.redhat.rcm.offliner.folo.StoreKey;

import java.io.IOException;

public final class StoreKeySerializer
    extends StdSerializer<StoreKey>
{
    public StoreKeySerializer()
    {
        super( StoreKey.class );
    }

    @Override
    public void serialize( final StoreKey key, final JsonGenerator generator, final SerializerProvider provider )
        throws IOException, JsonProcessingException
    {
        generator.writeString( key.toString() );
    }
}