import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Zero Boilerplate',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Annotate your components, systems, and interactions with a single
        annotation. Aergia generates all registration and codec code at compile
        time — no manual wiring required.
      </>
    ),
  },
  {
    title: 'Compile-Time Safety',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Powered by KSP (Kotlin Symbol Processing), all code generation happens
        at compile time. Errors are caught early, and generated code is fully
        type-safe.
      </>
    ),
  },
  {
    title: 'Focus on Mod Logic',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Stop writing repetitive registration code. Aergia handles{' '}
        <code>@RegisterComponent</code>, <code>@RegisterSystem</code>,{' '}
        <code>@RegisterInteraction</code>, and <code>@GenerateCodec</code> so
        you can focus on the creative work.
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
