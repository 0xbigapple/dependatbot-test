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

package org.tron.trident.abi.datatypes;

import java.util.List;

/**
 * Dynamic array type.
 */
public class DynamicArray<T extends Type> extends Array<T> {

  @Deprecated
  @SafeVarargs
  @SuppressWarnings({"unchecked"})
  public DynamicArray(T... values) {
    super((Class<T>) AbiTypes.getType(values[0].getTypeAsString()), values);
  }

  @Deprecated
  @SuppressWarnings("unchecked")
  public DynamicArray(List<T> values) {
    super((Class<T>) AbiTypes.getType(values.get(0).getTypeAsString()), values);
  }

  @Deprecated
  @SuppressWarnings("unchecked")
  private DynamicArray(String type) {
    super((Class<T>) AbiTypes.getType(type));
  }

  @Deprecated
  public static DynamicArray empty(String type) {
    return new DynamicArray(type);
  }

  public DynamicArray(Class<T> type, List<T> values) {
    super(type, values);
  }

  @Override
  public int bytes32PaddedLength() {
    return super.bytes32PaddedLength() + MAX_BYTE_LENGTH;
  }

  @SafeVarargs
  public DynamicArray(Class<T> type, T... values) {
    super(type, values);
  }

  @Override
  public String getTypeAsString() {
    return AbiTypes.getTypeAString(getComponentType()) + "[]";
  }
}
