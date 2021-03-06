/* Copyright 2016 Google Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.api.codegen.csharp;

import com.google.api.codegen.CodegenContext;
import com.google.api.codegen.GeneratedResult;
import com.google.api.codegen.SnippetSetRunner;
import com.google.api.tools.framework.snippet.Doc;
import com.google.api.tools.framework.snippet.SnippetSet;
import com.google.common.collect.ImmutableMap;

import java.util.TreeSet;

/**
 * A CSharpProvider provides general CSharp code generation logic.
 */
public class CSharpSnippetSetRunner<ElementT> implements SnippetSetRunner<ElementT> {

  /**
   * The path to the root of snippet resources.
   */
  private static final String SNIPPET_RESOURCE_ROOT =
      CSharpContextCommon.class.getPackage().getName().replace('.', '/');

  @Override
  @SuppressWarnings("unchecked")
  public GeneratedResult generate(
      ElementT element, String snippetFileName, CodegenContext context) {
    CSharpSnippetSet<ElementT> snippets =
        SnippetSet.createSnippetInterface(
            CSharpSnippetSet.class,
            SNIPPET_RESOURCE_ROOT,
            snippetFileName,
            ImmutableMap.<String, Object>of("context", context));

    String outputFilename = snippets.generateFilename(element).prettyPrint();
    CSharpContextCommon csharpContextCommon = new CSharpContextCommon();

    // TODO don't depend on a cast here
    CSharpContext csharpContext = (CSharpContext) context;
    csharpContext.resetState(csharpContextCommon);

    // Generate the body, which will collect the imports.
    Doc body = snippets.generateBody(element);

    TreeSet<String> cleanedImports = csharpContextCommon.getImports();

    // Generate result.
    Doc result = snippets.generateClass(element, body, cleanedImports);
    return GeneratedResult.create(result, outputFilename);
  }
}
