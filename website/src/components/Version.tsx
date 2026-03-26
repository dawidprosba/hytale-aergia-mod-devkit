import useDocusaurusContext from '@docusaurus/useDocusaurusContext';

export default function Version(): string {
  const {siteConfig} = useDocusaurusContext();
  return siteConfig.customFields?.version as string;
}
