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
