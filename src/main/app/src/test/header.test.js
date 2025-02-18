import React from "react";
import {render, unmountComponentAtNode} from "react-dom";
import {act} from "react-dom/test-utils";
import renderer from 'react-test-renderer';
import Header from "../Header";

describe('Header Import Testsuite', () => {

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

  it("Test Header Rendering", () => {
    act(() => {
      render(<Header title="Handschriftenportal Erfassung"/>, container);
    });
    expect(container.textContent).toBe("Handschriftenportal Erfassung");
  })

  it('Header Snapshot Test', () => {
    const tree = renderer
    .create(<Header title="Handschriftenportal Erfassung"/>)
    .toJSON();
    expect(tree).toMatchSnapshot();
  });
});
