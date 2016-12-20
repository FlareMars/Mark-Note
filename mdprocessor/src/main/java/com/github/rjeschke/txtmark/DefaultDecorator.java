/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
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
package com.github.rjeschke.txtmark;

/**
 * Default Decorator implementation.
 * 
 * <p>
 * Example for a user Decorator having a class attribute on &lt;p> tags.
 * </p>
 * 
 * <pre>
 * <code>public class MyDecorator extends DefaultDecorator
 * {
 *     &#64;Override
 *     public void openParagraph(StringBuilder out)
 *     {
 *         out.append("&lt;p class=\"myclass\">");
 *     }
 * }
 * </code>
 * </pre>
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
public class DefaultDecorator implements Decorator
{
    /** Constructor. */
    public DefaultDecorator()
    {
        // empty
    }

    /** @see Decorator#openParagraph(StringBuilder) */
    @Override
    public void openParagraph(StringBuilder out)
    {
        out.append("<p>");
    }

    /** @see Decorator#closeParagraph(StringBuilder) */
    @Override
    public void closeParagraph(StringBuilder out)
    {
        out.append("</p>\n");
    }

    /** @see Decorator#openBlockquote(StringBuilder) */
    @Override
    public void openBlockquote(StringBuilder out)
    {
        out.append("<blockquote>");
    }

    /** @see Decorator#closeBlockquote(StringBuilder) */
    @Override
    public void closeBlockquote(StringBuilder out)
    {
        out.append("</blockquote>\n");
    }

    /** @see Decorator#openCodeBlock(StringBuilder) */
    @Override
    public void openCodeBlock(StringBuilder out)
    {
        out.append("<pre><code>");
    }

    /** @see Decorator#closeCodeBlock(StringBuilder) */
    @Override
    public void closeCodeBlock(StringBuilder out)
    {
        out.append("</code></pre>\n");
    }

    /** @see Decorator#openCodeSpan(StringBuilder) */
    @Override
    public void openCodeSpan(StringBuilder out)
    {
        out.append("<code>");
    }

    /** @see Decorator#closeCodeSpan(StringBuilder) */
    @Override
    public void closeCodeSpan(StringBuilder out)
    {
        out.append("</code>");
    }

    /**
     * @see Decorator#openHeadline(StringBuilder,
     *      int)
     */
    @Override
    public void openHeadline(StringBuilder out, int level)
    {
        out.append("<h");
        out.append(level);
    }

    /**
     * @see Decorator#closeHeadline(StringBuilder,
     *      int)
     */
    @Override
    public void closeHeadline(StringBuilder out, int level)
    {
        out.append("</h");
        out.append(level);
        out.append(">\n");
    }

    /** @see Decorator#openStrong(StringBuilder) */
    @Override
    public void openStrong(StringBuilder out)
    {
        out.append("<strong>");
    }

    /** @see Decorator#closeStrong(StringBuilder) */
    @Override
    public void closeStrong(StringBuilder out)
    {
        out.append("</strong>");
    }

    /** @see Decorator#openStrong(StringBuilder) */
    @Override
    public void openStrike(StringBuilder out)
    {
        out.append("<s>");
    }

    /** @see Decorator#closeStrong(StringBuilder) */
    @Override
    public void closeStrike(StringBuilder out)
    {
        out.append("</s>");
    }

    /** @see Decorator#openEmphasis(StringBuilder) */
    @Override
    public void openEmphasis(StringBuilder out)
    {
        out.append("<em>");
    }

    /** @see Decorator#closeEmphasis(StringBuilder) */
    @Override
    public void closeEmphasis(StringBuilder out)
    {
        out.append("</em>");
    }

    /** @see Decorator#openSuper(StringBuilder) */
    @Override
    public void openSuper(StringBuilder out)
    {
        out.append("<sup>");
    }

    /** @see Decorator#closeSuper(StringBuilder) */
    @Override
    public void closeSuper(StringBuilder out)
    {
        out.append("</sup>");
    }

    /** @see Decorator#openOrderedList(StringBuilder) */
    @Override
    public void openOrderedList(StringBuilder out)
    {
        out.append("<ol>\n");
    }

    /** @see Decorator#closeOrderedList(StringBuilder) */
    @Override
    public void closeOrderedList(StringBuilder out)
    {
        out.append("</ol>\n");
    }

    /** @see Decorator#openUnorderedList(StringBuilder) */
    @Override
    public void openUnorderedList(StringBuilder out)
    {
        out.append("<ul>\n");
    }

    /** @see Decorator#closeUnorderedList(StringBuilder) */
    @Override
    public void closeUnorderedList(StringBuilder out)
    {
        out.append("</ul>\n");
    }

    /** @see Decorator#openListItem(StringBuilder) */
    @Override
    public void openListItem(StringBuilder out)
    {
        out.append("<li");
    }

    /** @see Decorator#closeListItem(StringBuilder) */
    @Override
    public void closeListItem(StringBuilder out)
    {
        out.append("</li>\n");
    }

    /** @see Decorator#horizontalRuler(StringBuilder) */
    @Override
    public void horizontalRuler(StringBuilder out)
    {
        out.append("<hr />\n");
    }

    /** @see Decorator#openLink(StringBuilder) */
    @Override
    public void openLink(StringBuilder out)
    {
        out.append("<a");
    }

    /** @see Decorator#openImage(StringBuilder) */
    @Override
    public void openImage(StringBuilder out)
    {
        out.append("<img");
    }
}
