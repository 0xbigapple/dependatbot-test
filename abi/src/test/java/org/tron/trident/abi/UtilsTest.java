/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tron.trident.abi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.DynamicArray;
import org.tron.trident.abi.datatypes.DynamicBytes;
import org.tron.trident.abi.datatypes.Fixed;
import org.tron.trident.abi.datatypes.Int;
import org.tron.trident.abi.datatypes.StaticArray;
import org.tron.trident.abi.datatypes.Ufixed;
import org.tron.trident.abi.datatypes.Uint;
import org.tron.trident.abi.datatypes.Utf8String;
import org.tron.trident.abi.datatypes.generated.Int64;
import org.tron.trident.abi.datatypes.generated.StaticArray2;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.abi.datatypes.generated.Uint64;

public class UtilsTest {

  @Test
  public void testGetTypeName() {
    assertEquals(Utils.getTypeName(new TypeReference<Uint>() {
    }), ("uint256"));
    assertEquals(Utils.getTypeName(new TypeReference<Int>() {
    }), ("int256"));
    assertEquals(Utils.getTypeName(new TypeReference<Ufixed>() {
    }), ("ufixed256"));
    assertEquals(Utils.getTypeName(new TypeReference<Fixed>() {
    }), ("fixed256"));

    assertEquals(Utils.getTypeName(new TypeReference<Uint64>() {
    }), ("uint64"));
    assertEquals(Utils.getTypeName(new TypeReference<Int64>() {
    }), ("int64"));
    assertEquals(Utils.getTypeName(new TypeReference<Bool>() {
    }), ("bool"));
    assertEquals(Utils.getTypeName(new TypeReference<Utf8String>() {
    }), ("string"));
    assertEquals(Utils.getTypeName(new TypeReference<DynamicBytes>() {
    }), ("bytes"));

    assertEquals(
        Utils.getTypeName(
            new TypeReference.StaticArrayTypeReference<StaticArray<Uint>>(5) {
            }),
        ("uint256[5]"));
    assertEquals(Utils.getTypeName(new TypeReference<DynamicArray<Uint>>() {
    }), ("uint256[]"));
  }

  @Test
  public void testTypeMap() {
    final List<BigInteger> input =
        Arrays.asList(BigInteger.ZERO, BigInteger.ONE, BigInteger.TEN);

    Assertions.assertEquals(
        Utils.typeMap(input, Uint256.class),
        (Arrays.asList(
            new Uint256(BigInteger.ZERO),
            new Uint256(BigInteger.ONE),
            new Uint256(BigInteger.TEN))));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testTypeMapNested() {
    List<BigInteger> innerList1 = Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2));
    List<BigInteger> innerList2 = Arrays.asList(BigInteger.valueOf(3), BigInteger.valueOf(4));

    final List<List<BigInteger>> input = Arrays.asList(innerList1, innerList2);

    StaticArray2<Uint256> staticArray1 =
        new StaticArray2<>(Uint256.class, new Uint256(1), new Uint256(2));

    StaticArray2<Uint256> staticArray2 =
        new StaticArray2<>(Uint256.class, new Uint256(3), new Uint256(4));

    List<StaticArray2> actual = Utils.typeMap(input, StaticArray2.class, Uint256.class);

    assertEquals(actual.get(0), (staticArray1));
    assertEquals(actual.get(1), (staticArray2));
  }

  @Test
  public void testTypeMapEmpty() {
    Assertions.assertEquals(Utils.typeMap(new ArrayList<>(), Uint256.class),
        (new ArrayList<Uint256>()));
  }
}
