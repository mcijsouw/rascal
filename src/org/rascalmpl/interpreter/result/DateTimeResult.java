/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Mark Hills - Mark.Hills@cwi.nl (CWI)
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
*******************************************************************************/
package org.rascalmpl.interpreter.result;

import static org.rascalmpl.interpreter.result.ResultFactory.bool;
import static org.rascalmpl.interpreter.result.ResultFactory.makeResult;

import java.util.Iterator;

import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IDateTime;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.exceptions.InvalidDateTimeException;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.interpreter.staticErrors.InvalidDateTimeComparison;
import org.rascalmpl.interpreter.staticErrors.UndeclaredField;
import org.rascalmpl.interpreter.staticErrors.UnexpectedType;
import org.rascalmpl.interpreter.staticErrors.UnsupportedOperation;
import org.rascalmpl.interpreter.utils.RuntimeExceptionFactory;
import org.rascalmpl.values.ValueFactoryFactory;

import com.ibm.icu.util.Calendar;

public class DateTimeResult extends ElementResult<IDateTime> {

	private static TypeFactory TF = TypeFactory.getInstance();
	private static IValueFactory VF = ValueFactoryFactory.getValueFactory();
	public static final TypeStore TS = new TypeStore();
	public static final Type Duration = TF.abstractDataType(TS, "Duration");
	public static final Type duration = TF.constructor(TS, Duration, "duration", 
			TF.integerType(), "years", TF.integerType(), "months", 
			TF.integerType(), "days", TF.integerType(), "hours", 
			TF.integerType(), "minutes", TF.integerType(), "seconds", 
			TF.integerType(), "milliseconds");
	
	public DateTimeResult(Type type, IDateTime value, IEvaluatorContext ctx) {
		super(type, value, ctx);
	}

	public DateTimeResult(Type type, IDateTime value, Iterator<Result<IValue>> iter, IEvaluatorContext ctx) {
		super(type, value, iter, ctx);
	}

	@Override
	public <V extends IValue> Result<IBool> equals(Result<V> that) {
		return that.equalToDateTime(this);
	}

	@Override
	protected Result<IBool> equalToDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return bool(that.value.getInstant() == this.value.getInstant(), ctx);
	}
		
	@Override
	public <V extends IValue> Result<IBool> nonEquals(Result<V> that) {
		return that.nonEqualToDateTime(this);
	}

	@Override
	protected Result<IBool> nonEqualToDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return bool(that.value.getInstant() != this.value.getInstant(), ctx);
	}

	@Override
	public <U extends IValue> Result<U> fieldAccess(String name, TypeStore store) {
		IValueFactory vf = getValueFactory();
		IDateTime dt = getValue();

		try {

			if (name.equals("year")) {
				if (!dt.isTime()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(dt.getYear()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the year on a time value",ctx.getCurrentAST());
			} else if (name.equals("month")) {
				if (!dt.isTime()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getMonthOfYear()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the month on a time value",ctx.getCurrentAST());
			} else if (name.equals("day")) {
				if (!dt.isTime()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getDayOfMonth()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the day on a time value",ctx.getCurrentAST());
			} else if (name.equals("hour")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getHourOfDay()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the hour on a date value",ctx.getCurrentAST());
			} else if (name.equals("minute")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getMinuteOfHour()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the minute on a date value",ctx.getCurrentAST());
			} else if (name.equals("second")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getSecondOfMinute()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the second on a date value",ctx.getCurrentAST());
			} else if (name.equals("millisecond")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getMillisecondsOfSecond()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the millisecond on a date value",ctx.getCurrentAST());
			} else if (name.equals("timezoneOffsetHours")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getTimezoneOffsetHours()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the timezone offset hours on a date value",ctx.getCurrentAST());
			} else if (name.equals("timezoneOffsetMinutes")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getTimezoneOffsetMinutes()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the timezone offset minutes on a date value",ctx.getCurrentAST());
			} else if (name.equals("century")) {
				if (!dt.isTime()) {
					return makeResult(getTypeFactory().integerType(), vf.integer(getValue().getCentury()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the century on a time value",ctx.getCurrentAST());
			} else if (name.equals("isDate")) {
				return makeResult(getTypeFactory().boolType(), vf.bool(getValue().isDate()), ctx);
			} else if (name.equals("isTime")) {
				return makeResult(getTypeFactory().boolType(), vf.bool(getValue().isTime()), ctx);
			} else if (name.equals("isDateTime")) {
				return makeResult(getTypeFactory().boolType(), vf.bool(getValue().isDateTime()), ctx);
			} else if (name.equals("justDate")) {
				if (!dt.isTime()) {
					return makeResult(getTypeFactory().dateTimeType(),
							vf.date(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the date component of a time value",ctx.getCurrentAST());
			} else if (name.equals("justTime")) {
				if (!dt.isDate()) {
					return makeResult(getTypeFactory().dateTimeType(),
							vf.time(dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfMinute(), 
									dt.getMillisecondsOfSecond(), dt.getTimezoneOffsetHours(),
									dt.getTimezoneOffsetMinutes()), ctx);
				}
				throw new UnsupportedOperation("Can not retrieve the time component of a date value",ctx.getCurrentAST());
			}
		} catch (InvalidDateTimeException e) {
			throw RuntimeExceptionFactory.illegalArgument(dt, ctx.getCurrentAST(), null, e.getMessage());

		}
		throw new UndeclaredField(name, getTypeFactory().dateTimeType(), ctx.getCurrentAST());

	}

	@Override
	public <U extends IValue, V extends IValue> Result<U> fieldUpdate(
			String name, Result<V> repl, TypeStore store) {
		
		Type replType = repl.getType();
		IValue replValue = repl.getValue();
		IDateTime dt = getValue();
		
		// Individual fields
		int year = dt.getYear();
		int month = dt.getMonthOfYear();
		int day = dt.getDayOfMonth();
		int hour = dt.getHourOfDay();
		int minute = dt.getMinuteOfHour();
		int second = dt.getSecondOfMinute();
		int milli = dt.getMillisecondsOfSecond();
		int tzOffsetHour = dt.getTimezoneOffsetHours();
		int tzOffsetMin = dt.getTimezoneOffsetMinutes();

		try {
			if (name.equals("year")) {
				if (dt.isTime()) {
					throw new UnsupportedOperation("Can not update the year on a time value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				year = ((IInteger) replValue).intValue();
			} else if (name.equals("month")) {
				if (dt.isTime()) {
					throw new UnsupportedOperation("Can not update the month on a time value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				month = ((IInteger) replValue).intValue();				
			} else if (name.equals("day")) {
				if (dt.isTime()) {
					throw new UnsupportedOperation("Can not update the day on a time value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				day = ((IInteger) replValue).intValue();				
			} else if (name.equals("hour")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the hour on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				hour = ((IInteger) replValue).intValue();				
			} else if (name.equals("minute")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the minute on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				minute = ((IInteger) replValue).intValue();				
			} else if (name.equals("second")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the second on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				second = ((IInteger) replValue).intValue();				
			} else if (name.equals("millisecond")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the millisecond on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				milli = ((IInteger) replValue).intValue();			
			} else if (name.equals("timezoneOffsetHours")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the timezone offset hours on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				tzOffsetHour = ((IInteger) replValue).intValue();				
			} else if (name.equals("timezoneOffsetMinutes")) {
				if (dt.isDate()) {
					throw new UnsupportedOperation("Can not update the timezone offset minutes on a date value",ctx.getCurrentAST());
				}			
				if (!replType.isInteger()) {
					throw new UnexpectedType(getTypeFactory().integerType(), replType, ctx.getCurrentAST());
				}
				tzOffsetMin = ((IInteger) replValue).intValue();				
			} else {
				throw new UndeclaredField(name, getTypeFactory().dateTimeType(), ctx.getCurrentAST());
			}

			IDateTime newdt = null;
			if (dt.isDate()) {
				newdt = getValueFactory().date(year, month, day);
			} else if (dt.isTime()) {
				newdt = getValueFactory().time(hour, minute, second, milli, tzOffsetHour, tzOffsetMin);
			} else {
				newdt = getValueFactory().datetime(year, month, day, hour, minute, second, milli, tzOffsetHour, tzOffsetMin);
			}
			
			return makeResult(getType(), newdt, ctx);
		} 
		catch (IllegalArgumentException e) {
			throw RuntimeExceptionFactory.illegalArgument(repl.getValue(), ctx.getCurrentAST(), null, "Cannot update field " + name + ", this would generate an invalid datetime value");
		} 	
		catch (InvalidDateTimeException e) {
			throw RuntimeExceptionFactory.illegalArgument(repl.getValue(), ctx.getCurrentAST(), null, e.getMessage());

		}
	}

	private void checkDateTimeComparison(DateTimeResult that) {
		if (that.getValue().isDate()) {
			if (this.getValue().isTime()) {
				throw new InvalidDateTimeComparison("date","time",ctx.getCurrentAST());
			} else if (this.getValue().isDateTime()) {
				throw new InvalidDateTimeComparison("date","datetime",ctx.getCurrentAST());				
			}
		} else if (that.getValue().isTime()) {
			if (this.getValue().isDate()) {
				throw new InvalidDateTimeComparison("time","date",ctx.getCurrentAST());				
			} else if (this.getValue().isDateTime()) {
				throw new InvalidDateTimeComparison("time","datetime",ctx.getCurrentAST());				
			}
		} else {
			if (this.getValue().isDate()) {
				throw new InvalidDateTimeComparison("datetime","date",ctx.getCurrentAST());
			} else if (this.getValue().isTime()) {
				throw new InvalidDateTimeComparison("datetime","time",ctx.getCurrentAST());				
			}
		}
	}

	@Override
	public <V extends IValue> Result<IBool> greaterThan(Result<V> that) {
		return that.greaterThanDateTime(this);
	}

	@Override 
	protected Result<IBool> greaterThanDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return bool(that.value.getInstant() > this.value.getInstant(), ctx);
	}

	@Override
	public <V extends IValue> Result<IBool> greaterThanOrEqual(Result<V> that) {
		return that.greaterThanOrEqualDateTime(this);
	}

	@Override
	protected Result<IBool> greaterThanOrEqualDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return bool(that.value.getInstant() >= this.value.getInstant(), ctx);
	}

	@Override
	public <V extends IValue> Result<IBool> lessThan(Result<V> that) {
		return that.lessThanDateTime(this);
	}

	@Override
	protected Result<IBool> lessThanDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return bool(that.value.getInstant() < this.value.getInstant(), ctx);
	}

	@Override
	public <V extends IValue> LessThanOrEqualResult lessThanOrEqual(Result<V> that) {
		return that.lessThanOrEqualDateTime(this);
	}

	@Override
	protected LessThanOrEqualResult lessThanOrEqualDateTime(DateTimeResult that) {
		checkDateTimeComparison(that);
		return new LessThanOrEqualResult(that.value.getInstant() < this.value.getInstant(), that.value.getInstant() == this.value.getInstant(), ctx);
	}

	@Override
	public <U extends IValue, V extends IValue> Result<U> subtract(Result<V> that) {
		return that.subtractDateTime(this);
	}

	@Override
	protected <U extends IValue> Result<U> subtractDateTime(DateTimeResult that) {
		IDateTime dStart = this.getValue();
		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(dStart.getInstant());

		IDateTime dEnd = that.getValue();
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(dEnd.getInstant());
		
		if (dStart.isDate()) {
			if (dEnd.isDate()) {
				return makeResult(Duration,
						VF.constructor(DateTimeResult.duration,
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.YEAR)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MONTH)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.DAY_OF_MONTH)),
							VF.integer(0), 
							VF.integer(0), 
							VF.integer(0),
							VF.integer(0)),
						ctx);
			} else if (dEnd.isTime()) {
				throw RuntimeExceptionFactory.invalidUseOfTimeException("Cannot determine the duration between a date with no time and a time with no date.", ctx.getCurrentAST(), null);	
			} else {
				throw RuntimeExceptionFactory.invalidUseOfDateTimeException("Cannot determine the duration between a date with no time and a datetime.", ctx.getCurrentAST(), null);					
			}
		} else if (dStart.isTime()) {
			if (dEnd.isTime()) {
				return makeResult(Duration,
						VF.constructor(DateTimeResult.duration,
							VF.integer(0),
							VF.integer(0),
							VF.integer(0),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.HOUR_OF_DAY)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MINUTE)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.SECOND)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MILLISECOND))),
						ctx);
			} else if (dEnd.isDate()) {
				throw RuntimeExceptionFactory.invalidUseOfDateException("Cannot determine the duration between a time with no date and a date with no time.", ctx.getCurrentAST(), null);	
			} else {
				throw RuntimeExceptionFactory.invalidUseOfDateTimeException("Cannot determine the duration between a time with no date and a datetime.", ctx.getCurrentAST(), null);					
			}
		} else {
			if (dEnd.isDateTime()) {
				return makeResult(Duration,
						VF.constructor(DateTimeResult.duration,
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.YEAR)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MONTH)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.DAY_OF_MONTH)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.HOUR_OF_DAY)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MINUTE)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.SECOND)),
							VF.integer(startCal.fieldDifference(endCal.getTime(), Calendar.MILLISECOND))),
						ctx);
			} else if (dEnd.isDate()) {
				throw RuntimeExceptionFactory.invalidUseOfDateException("Cannot determine the duration between a datetime and a date with no time.", ctx.getCurrentAST(), null);	
			} else {
				throw RuntimeExceptionFactory.invalidUseOfTimeException("Cannot determine the duration between a datetime and a time with no date.", ctx.getCurrentAST(), null);					
			}
		}
	}
	
	@Override
	protected <U extends IValue> Result<U> addListRelation(ListRelationResult that) {
		return that.addDateTime(this);
	}
	
	@Override
	protected <U extends IValue> Result<U> addRelation(RelationResult that) {
		return that.addDateTime(this);
	}
	
}
