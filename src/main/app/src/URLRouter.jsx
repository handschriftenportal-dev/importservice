/*
 * MIT License
 *
 * Copyright (c) 2023 Staatsbibliothek zu Berlin - Preußischer Kulturbesitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import React from "react";
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import Import from "./Import";
import ImportJobDetail from "./domain/ImportJobDetail"
import NotFound from "./infrastructure/NotFound";
import {
  ApplicationConfigurationContext,
  configuration
} from "./infrastructure/ApplicationConfigurationContext";

export function urlRouter() {
  return (

      <ApplicationConfigurationContext.Provider value={configuration}>
        <Router>
          <Switch>
            <Route path="/app" exact component={Import}></Route>
            <Route path="/app/import/job/:id"
                   component={ImportJobDetail}></Route>
            <Route path="*" exact component={NotFound}></Route>
          </Switch>
        </Router>
      </ApplicationConfigurationContext.Provider>
  );
}
