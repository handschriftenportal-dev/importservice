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
import {unmountComponentAtNode} from "react-dom";
import {act} from "react-dom/test-utils";
import UploadForm from "../domain/UploadForm";
import createComponentWithIntl from './createComponentWithIntl';

describe('Import Upload Form Testsuite', () => {

  let container = null;
  beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
  });

  afterEach(() => {
    unmountComponentAtNode(container);
    container.remove();
    container = null;
  });

  it("Test UploadFrom Expected Content", () => {

    let form = createComponentWithIntl(<UploadForm id="uploadform"
                                                   tableUpdate={() => {
                                                   }}
                                                   endpoint="/rest/import"></UploadForm>),
        container;

    //expect(form.exists("#uploadheadline")).to.equal(true);

    expect(form.root.findAllByType("button")).toHaveLength(2);

  })

  it('UploadForm Snapshot Test', () => {
    const tree =
        createComponentWithIntl(<UploadForm id="uploadform"
                                            tableUpdate={() => {
                                            }}
                                            endpoint="http://b-dev1047.pk.de:9296/rest/import"></UploadForm>)
        .toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('UploadForm Click Submit Button', () => {
    const onClick = jest.fn();
    const jsdomAlert = window.alert;
    window.alert = () => {
    };

    act(() => {
      createComponentWithIntl(<UploadForm id="uploadform"
                                          tableUpdate={onClick}
                                          endpoint="http://b-dev1047.pk.de:9296/rest/import"></UploadForm>),
          container;
    });

    /* const button = document.querySelector("#submitupload");

     act(() => {
       button.dispatchEvent(new MouseEvent("click", {bubbles: true}));
     });

     expect(onClick).toHaveBeenCalledTimes(0);*/

  });

});
