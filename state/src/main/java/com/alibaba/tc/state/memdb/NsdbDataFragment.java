/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.tc.state.memdb;

import com.alibaba.sdb.spi.HostAddress;
import com.facebook.airlift.json.JsonCodec;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import static com.facebook.airlift.json.JsonCodec.jsonCodec;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class NsdbDataFragment
{
    private static final JsonCodec<NsdbDataFragment> MEMORY_DATA_FRAGMENT_CODEC = jsonCodec(NsdbDataFragment.class);

    private final HostAddress hostAddress;
    private final long rows;

    @JsonCreator
    public NsdbDataFragment(
            @JsonProperty("hostAddress") HostAddress hostAddress,
            @JsonProperty("rows") long rows)
    {
        this.hostAddress = requireNonNull(hostAddress, "hostAddress is null");
        checkArgument(rows >= 0, "Rows number can not be negative");
        this.rows = rows;
    }

    @JsonProperty
    public HostAddress getHostAddress()
    {
        return hostAddress;
    }

    @JsonProperty
    public long getRows()
    {
        return rows;
    }

    public Slice toSlice()
    {
        return Slices.wrappedBuffer(MEMORY_DATA_FRAGMENT_CODEC.toJsonBytes(this));
    }

    public static NsdbDataFragment fromSlice(Slice fragment)
    {
        return MEMORY_DATA_FRAGMENT_CODEC.fromJson(fragment.getBytes());
    }

    public static NsdbDataFragment merge(NsdbDataFragment a, NsdbDataFragment b)
    {
        checkArgument(a.getHostAddress().equals(b.getHostAddress()), "Can not merge fragments from different hosts");
        return new NsdbDataFragment(a.getHostAddress(), a.getRows() + b.getRows());
    }
}