/*
 * Copyright (c) 2011. Geovise BVBA, QMINO BVBA
 *
 * This file is part of GeoLatte Mapserver.
 *
 * GeoLatte Mapserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeoLatte Mapserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GeoLatte Mapserver.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.geolatte.maprenderer.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectionHelper {
	
	
	public static Object getObjectValue(Object in, Field fld){
		for ( Method m : in.getClass().getMethods()){
			if (m.getName().equals("get" + capitalize(fld.getName())) &&
					m.getParameterTypes().length == 0){
				try {
					return m.invoke(in, (Object[])null);
				} catch (Exception e) {
					throw new RuntimeException("Problem getting value for fld " + fld.getName() + " for type " + in.getClass().getCanonicalName(), e);
				}
			}
		}
		throw new RuntimeException("Couldn't find getter for " + fld.getName() + " in type " + in.getClass().getCanonicalName());
	}

	public static void setObjectValue(Object obj, Field fld, Object value){
		for ( Method m : obj.getClass().getMethods()){
			if (m.getName().equals("set" + capitalize(fld.getName())) && 
					m.getParameterTypes().length == 1 &&
					m.getParameterTypes()[0] == value.getClass()){
				try{
					m.invoke(obj, new Object[]{value});
					return;
				} catch(Exception e){
					throw new RuntimeException("Problem setting value for fld " + fld.getName() + " for type " + obj.getClass().getCanonicalName(), e);
				}
			}
		}
		throw new RuntimeException("Couldn't find setter for " + fld.getName() + " and value of type " + value.getClass().getSimpleName() + " in type " + obj.getClass().getCanonicalName());
	}
	
	
	@SuppressWarnings("unchecked")
	public static void addObjectValue(Object obj, Field fld, Object value){
		for ( Method m : obj.getClass().getMethods()){
			if (m.getName().equals("get" + capitalize(fld.getName()))){
				try{
					Collection<Object> c = (Collection<Object>)m.invoke(obj, (Object[])null);
					c.add(value);
					return;
				} catch(Exception e){
					throw new RuntimeException("Problem setting value for fld " + fld.getName() + " for type " + obj.getClass().getCanonicalName(), e);
				}
			}
		}
		throw new RuntimeException("Couldn't find setter for " + fld.getName() + " and value of type " + value.getClass().getSimpleName() + " in type " + obj.getClass().getCanonicalName());
		
	}

	
	/**
	 * Transfer all 
	 * @param in
	 * @param out
	 */
	public static void automaticTransfer(Object in, Object out){

		Method[] getters = ReflectionHelper.getGetters(in.getClass());
		
		for (Method getter : getters){
			try{
				Object vtt = getter.invoke(in, (Object[]) null);			
				if (vtt instanceof Collection){
					continue;
					// do this later
				} 
				Method setter = corresponding(getter,out.getClass());
				if (setter == null){
					continue;
				}
				setter.invoke(out,new Object[]{vtt});
			} catch(Exception e){
				System.err.println(e);
			}
			
		}
		
	}
	
	/**
	 * locate the field in the input Field array flds that 
	 * has same name and type as type inFld.
	 *  
	 * @param inFld
	 * @param flds
	 * @return
	 */
	public static Field corresponding (Field inFld, Field[] flds){
		for (Field fld : flds){
			if (fld.getType() == inFld.getType() &&
					fld.getName().equals(inFld.getName())){
				return fld;
			}
		}
		return null;
	}
	
	/**
	 * Given a getter, locate a setter for the same type of
	 * variable.
	 * 
	 * @param m
	 * @param clazz
	 * @return
	 */
	private static Method corresponding(Method getter, Class clazz){
		for (Method m : clazz.getMethods()){
			if (m.getName().startsWith("set") &&
					getter.getName().substring(3).equals(m.getName().substring(3)) && 
					m.getParameterTypes().length == 1	&&
					m.getParameterTypes()[0].isAssignableFrom(getter.getReturnType())){
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Gets all the public, protected, private or default fields
	 * declared in the specified class or its superclasses.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Field[] getAllFields(Class clazz){
		List<Field> allFlds =  getAllFields(clazz, new ArrayList<Field>());
		return allFlds.toArray(new Field[allFlds.size()]);
	}
	
	private static List<Field> getAllFields(Class clazz, List<Field> fields){
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		Class supercls = clazz.getSuperclass();
		if (supercls == null){
			return fields;
		} else {
			return getAllFields(supercls,fields);
		}		
	}
	
	
	public static Method[] getGetters(Class clazz){	
		List<Method> outM = new ArrayList<Method>();
		for (Method m : clazz.getMethods()){
			if (m.getParameterTypes().length == 0 &&
					m.getName().startsWith("get") &&
					m.getReturnType() != null){
				outM.add(m);
			}
		}
		return outM.toArray(new Method[outM.size()]);
	}
	
	public static Method[] getSetters(Class clazz){	
		List<Method> outM = new ArrayList<Method>();
		for (Method m : clazz.getMethods()){
			if (m.getParameterTypes().length == 1 &&
					m.getName().startsWith("set") &&
					m.getReturnType() == null){
				outM.add(m);
			}
		}
		return outM.toArray(new Method[outM.size()]);
	}
	
	
	private static String capitalize(String in){
		String tail = in.substring(1);
		String head = in.substring(0,1);
		return head.toUpperCase() + tail;
	}
	
}
