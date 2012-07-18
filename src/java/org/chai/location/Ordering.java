package org.chai.location;
/** 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.util.Comparator;

import javax.persistence.Embeddable;

import org.chai.location.json.JSONMap;


@Embeddable
public class Ordering extends JSONMap<Integer> implements Comparable<Ordering>, Exportable, Importable {

	private static final long serialVersionUID = 1179476928310670136L;

	public Ordering() {
		super();
	}
	
	public Ordering(JSONMap<Integer> jsonMap) {
		super(jsonMap);
	}
	
	private Integer getOrder(String currentLanguage, String fallbackLanguage) {
		if (containsKey(currentLanguage)) return get(currentLanguage);
		else return get(fallbackLanguage);
	}

	public static class OrderingComparator implements Comparator<Ordering> {

		private String currentLanguage;
		private String fallbackLanguage;
		
		public OrderingComparator(String currentLanguage, String fallbackLanguage) {
			this.currentLanguage = currentLanguage;
			this.fallbackLanguage = fallbackLanguage;
		}
		
		@Override
		public int compare(Ordering ordering0, Ordering ordering1) {
			if (ordering0.getOrder(currentLanguage, fallbackLanguage) == null 
				&& ordering1.getOrder(currentLanguage, fallbackLanguage) == null) {
				return 0;
			}
			else if (ordering0.getOrder(currentLanguage, fallbackLanguage) == null) {
				return -1;
			}
			else if (ordering1.getOrder(currentLanguage, fallbackLanguage) == null) {
				return 1;
			}
			else return ordering0.getOrder(currentLanguage, fallbackLanguage).compareTo(ordering1.getOrder(currentLanguage, fallbackLanguage));
		}
		
	}
	
	public static Comparator<Orderable<Ordering>> getOrderableComparator(final String currentLanguage, final String fallbackLanguage) {
		return new Comparator<Orderable<Ordering>>(){
			private OrderingComparator orderingComparator = new OrderingComparator(currentLanguage, fallbackLanguage);
			@Override
			public int compare(Orderable<Ordering> arg0, Orderable<Ordering> arg1) {
				return orderingComparator.compare(arg0.getOrder(), arg1.getOrder());
			}
		};
	}

	@Override
	public int compareTo(Ordering o) {
		return 0;
	}
	
	@Override
	public String toExportString() {
		return super.toExportString();
	}

	@Override
	public Ordering fromExportString(Object value) {
		JSONMap jsonMap = super.fromExportString(value);
		Ordering ordering = new Ordering(jsonMap);
		return ordering;
	}
}