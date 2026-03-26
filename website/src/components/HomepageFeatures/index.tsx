import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import Link from '@docusaurus/Link';
import styles from './styles.module.css';

type FeatureItem = {
    title: string;
    Svg: React.ComponentType<React.ComponentProps<'svg'>>;
    description: ReactNode;
};

const FeatureList: FeatureItem[] = [
    {
        title: 'Reduce Boilerplate',
        Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
        description: (
            <>
                Use Annotations to reduce boilerplate and focus on mod logic.
            </>
        ),
    },
    {
        title: 'Kotlin',
        Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
        description: (
            <>
                This is intended to work with Kotlin. If you wish to migrate your plugin to {" "}
                <a href="https://kotlinlang.org/" target="_blank">Kotlin</a> {" "}
                you can follow <Link to="/docs/java-to-kotlin">this short guide</Link>
            </>
        ),
    },
    {
        title: 'Why this devkit exsists?',
        Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
        description: (
            <>
                Because I'm lazy as <a href="https://en.wikipedia.org/wiki/Aergia" target="_blank">Aergia Goddess.</a>
            </>
        ),
    },

];

function Feature({title, Svg, description}: FeatureItem) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img"/>
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
