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
import UploadFileTable from '../domain/UploadFileTable'
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

  it("Test Upload File Table ", () => {

    const table = createComponentWithIntl(<UploadFileTable data={[{
      "id": "6709b9a0-44a8-4bcd-85ab-0b80a818c5fe",
      "datum": "2020-03-23T16:46:22.627314",
      "benutzer": "Christoph Mackert",
      "dateianzahl": 1,
      "dateiname": "tei-msDesc_Koch.xml",
      "ergebnis": "SUCCESS"
    }]} headlines={['Upload am', 'Benutzer',
      'Anzahl Dateien',
      'Importdatei', 'Ergebnis']}/>, container);

    expect(table.root.findAllByType('table')).toHaveLength(1)
  })

  it("Test Upload File Table Snapshot", () => {

    const table = createComponentWithIntl(<UploadFileTable data={[{
      "id": "6709b9a0-44a8-4bcd-85ab-0b80a818c5fe",
      "datum": "2020-03-23T16:46:22.627314",
      "benutzer": "Christoph Mackert",
      "dateianzahl": 1,
      "dateiname": "tei-msDesc_Koch.xml",
      "ergebnis": "SUCCESS"
    }]} headlines={['Upload am', 'Benutzer',
      'Anzahl Dateien',
      'Importdatei', 'Ergebnis']}/>, container).toJSON();

    expect(table).toMatchSnapshot();
  })
});
