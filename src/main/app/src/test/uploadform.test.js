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
