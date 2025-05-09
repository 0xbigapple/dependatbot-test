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

import java.math.BigInteger;
import java.util.List;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.StaticArray;
import org.tron.trident.abi.datatypes.Type;
import org.tron.trident.abi.datatypes.Uint;

public class DefaultFunctionEncoder extends FunctionEncoder {

  @Override
  public String encodeFunction(final Function function) {
    final List<Type> parameters = function.getInputParameters();

    final String methodSignature = buildMethodSignature(function.getName(), parameters);
    final String methodId = buildMethodId(methodSignature);

    final StringBuilder result = new StringBuilder();
    result.append(methodId);

    return encodeParameters(parameters, result);
  }

  @Override
  public String encodeParameters(final List<Type> parameters) {
    return encodeParameters(parameters, new StringBuilder());
  }

  private static String encodeParameters(
      final List<Type> parameters, final StringBuilder result) {

    int dynamicDataOffset = getLength(parameters) * Type.MAX_BYTE_LENGTH;
    final StringBuilder dynamicData = new StringBuilder();

    for (Type parameter : parameters) {
      final String encodedValue = TypeEncoder.encode(parameter);

      if (TypeEncoder.isDynamic(parameter)) {
        final String encodedDataOffset =
            TypeEncoder.encodeNumeric(new Uint(BigInteger.valueOf(dynamicDataOffset)));
        result.append(encodedDataOffset);
        dynamicData.append(encodedValue);
        dynamicDataOffset += encodedValue.length() >> 1;
      } else {
        result.append(encodedValue);
      }
    }
    result.append(dynamicData);

    return result.toString();
  }

  private static int getLength(final List<Type> parameters) {
    int count = 0;
    for (final Type type : parameters) {
      if (type instanceof StaticArray) {
        count += ((StaticArray) type).getValue().size();
      } else {
        count++;
      }
    }
    return count;
  }
}
