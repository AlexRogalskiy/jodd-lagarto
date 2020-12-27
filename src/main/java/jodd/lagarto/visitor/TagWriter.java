// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package jodd.lagarto.visitor;

import jodd.lagarto.Doctype;
import jodd.lagarto.LagartoException;
import jodd.lagarto.Tag;
import jodd.lagarto.TagType;
import jodd.lagarto.TagVisitor;
import jodd.net.HtmlEncoder;
import jodd.util.StringUtil;

import java.io.IOException;

/**
 * Tag writer outputs content to an {@code Appendable}.
 */
public class TagWriter implements TagVisitor {

	protected final Appendable appendable;

	public TagWriter(final Appendable appendable) {
		this.appendable = appendable;
	}

	public TagWriter() {
		this.appendable = new StringBuilder();
	}

	/**
	 * Returns the content.
	 */
	public Appendable getOutput() {
		return appendable;
	}

	// ---------------------------------------------------------------- visitor

	@Override
	public void start() {
	}

	@Override
	public void end() {
	}

	private boolean isRawTag;

	@Override
	public void tag(final Tag tag) {
		tag.writeTo(appendable);
		isRawTag = tag.isRawTag();
	}

	@Override
	public void script(final Tag tag, final CharSequence body) {
		try {
			tag.setType(TagType.START);
			tag.writeTo(appendable);
			if (StringUtil.isNotEmpty(body)) {
				appendable.append(body);
			}
			appendable.append("</script>");
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void comment(final CharSequence comment) {
		try {
			TagWriterUtil.writeComment(appendable, comment);
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void text(final CharSequence text) {
		try {
			if (isRawTag) {
				appendable.append(text);
			} else {
				appendable.append(HtmlEncoder.text(text));
			}
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void cdata(final CharSequence cdata) {
		try {
			TagWriterUtil.writeCData(appendable, cdata);
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void xml(final CharSequence version, final CharSequence encoding, final CharSequence standalone) {
		try {
			TagWriterUtil.writeXml(appendable, version, encoding, standalone);
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void doctype(final Doctype doctype) {
		try {
			TagWriterUtil.writeDoctype(
					appendable,
					doctype.getName(),
					doctype.getPublicIdentifier(),
					doctype.getSystemIdentifier());
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void condComment(final CharSequence expression, final boolean isStartingTag, final boolean isHidden, final boolean isHiddenEndTag) {
		try {
			TagWriterUtil.writeConditionalComment(appendable, expression, isStartingTag, isHidden, isHiddenEndTag);
		} catch (final IOException ioex) {
			throw new LagartoException(ioex);
		}
	}

	@Override
	public void error(final String message) {
	}

}
