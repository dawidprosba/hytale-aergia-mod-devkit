import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    'intro',
    {
      type: 'category',
      label: 'Getting Started',
      link: {type: 'doc', id: 'getting-started'},
      items: [
        {type: 'link', label: 'Installation', href: '/docs/getting-started#installation'},
        {type: 'link', label: 'Wire up the generated registries', href: '/docs/getting-started#wire-up-the-generated-registries'},
      ],
    },
    'java-to-kotlin',
  ],
};

export default sidebars;
