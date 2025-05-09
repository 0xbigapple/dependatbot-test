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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.tron.trident.abi.datatypes.DynamicArray;
import org.tron.trident.abi.datatypes.DynamicBytes;
import org.tron.trident.abi.datatypes.Fixed;
import org.tron.trident.abi.datatypes.Int;
import org.tron.trident.abi.datatypes.StaticArray;
import org.tron.trident.abi.datatypes.Type;
import org.tron.trident.abi.datatypes.Ufixed;
import org.tron.trident.abi.datatypes.Uint;
import org.tron.trident.abi.datatypes.Utf8String;

/**
 * Utility functions.
 */
public class Utils {

  private Utils() {
  }

  static <T extends Type> String getTypeName(TypeReference<T> typeReference) {
    try {
      java.lang.reflect.Type reflectedType = typeReference.getType();

      Class<?> type;
      if (reflectedType instanceof ParameterizedType) {
        type = (Class<?>) ((ParameterizedType) reflectedType).getRawType();
        return getParameterizedTypeName(typeReference, type);
      } else {
        type = Class.forName(reflectedType.getTypeName());
        return getSimpleTypeName(type);
      }
    } catch (ClassNotFoundException e) {
      throw new UnsupportedOperationException("Invalid class reference provided", e);
    }
  }

  static String getSimpleTypeName(Class<?> type) {
    String simpleName = type.getSimpleName().toLowerCase();

    if (type.equals(Uint.class)
        || type.equals(Int.class)
        || type.equals(Ufixed.class)
        || type.equals(Fixed.class)) {
      return simpleName + "256";
    } else if (type.equals(Utf8String.class)) {
      return "string";
    } else if (type.equals(DynamicBytes.class)) {
      return "bytes";
    } else {
      return simpleName;
    }
  }

  static <T extends Type, U extends Type> String getParameterizedTypeName(
      TypeReference<T> typeReference, Class<?> type) {

    try {
      if (type.equals(DynamicArray.class)) {
        Class<U> parameterizedType = getParameterizedTypeFromArray(typeReference);
        String parameterizedTypeName = getSimpleTypeName(parameterizedType);
        return parameterizedTypeName + "[]";
      } else if (type.equals(StaticArray.class)) {
        Class<U> parameterizedType = getParameterizedTypeFromArray(typeReference);
        String parameterizedTypeName = getSimpleTypeName(parameterizedType);
        return parameterizedTypeName
            + "["
            + ((TypeReference.StaticArrayTypeReference) typeReference).getSize()
            + "]";
      } else {
        throw new UnsupportedOperationException("Invalid type provided " + type.getName());
      }
    } catch (ClassNotFoundException e) {
      throw new UnsupportedOperationException("Invalid class reference provided", e);
    }
  }

  @SuppressWarnings("unchecked")
  static <T extends Type> Class<T> getParameterizedTypeFromArray(TypeReference typeReference)
      throws ClassNotFoundException {

    java.lang.reflect.Type type = typeReference.getType();
    java.lang.reflect.Type[] typeArguments =
        ((ParameterizedType) type).getActualTypeArguments();

    String parameterizedTypeName = typeArguments[0].getTypeName();
    return (Class<T>) Class.forName(parameterizedTypeName);
  }

  @SuppressWarnings("unchecked")
  public static List<TypeReference<Type>> convert(List<TypeReference<?>> input) {
    List<TypeReference<Type>> result = new ArrayList<>(input.size());
    result.addAll(
        input.stream()
            .map(typeReference -> (TypeReference<Type>) typeReference)
            .collect(Collectors.toList()));
    return result;
  }

  public static <T, R extends Type<T>, E extends Type<T>> List<E> typeMap(
      List<List<T>> input, Class<E> outerDestType, Class<R> innerType) {
    List<E> result = new ArrayList<>();
    try {
      Constructor<E> constructor =
          outerDestType.getDeclaredConstructor(Class.class, List.class);
      for (List<T> ts : input) {
        E e = constructor.newInstance(innerType, typeMap(ts, innerType));
        result.add(e);
      }
    } catch (NoSuchMethodException
             | IllegalAccessException
             | InstantiationException
             | InvocationTargetException e) {
      throw new TypeMappingException(e);
    }
    return result;
  }

  public static <T, R extends Type<T>> List<R> typeMap(List<T> input, Class<R> destType)
      throws TypeMappingException {

    List<R> result = new ArrayList<>(input.size());

    if (!input.isEmpty()) {
      try {
        Constructor<R> constructor =
            destType.getDeclaredConstructor(input.get(0).getClass());
        for (T value : input) {
          result.add(constructor.newInstance(value));
        }
      } catch (NoSuchMethodException
               | IllegalAccessException
               | InvocationTargetException
               | InstantiationException e) {
        throw new TypeMappingException(e);
      }
    }
    return result;
  }
}
