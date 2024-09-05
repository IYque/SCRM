import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'

import AutoImport from 'unplugin-auto-import/vite' // 自动导入ref等api
// import Components from 'unplugin-vue-components/vite'
// import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import rollupPluginVisualizer from 'rollup-plugin-visualizer'
import vitePluginCompression from 'vite-plugin-compression'
import { ViteImageOptimizer } from 'vite-plugin-image-optimizer'

// https://vitejs.dev/config/
export default defineConfig(async ({ command, mode }) => {
	process.env.VUE_APP_ENV = mode
	let { env } = await import('./sys.config')

	return {
		define: {
			'process.env.VUE_APP_ENV': JSON.stringify(mode),
			__PACK_DATETIME__: JSON.stringify(new Date().toLocaleString()),
		},
		base: env.BASE_URL || '/',

		resolve: {
			// https://cn.vitejs.dev/config/#resolve-alias
			alias: {
				'@': fileURLToPath(new URL('./src', import.meta.url)),
				'/@': fileURLToPath(new URL('./src', import.meta.url)),
				'~': fileURLToPath(new URL('./src/components', import.meta.url)),
				// vue: '@vue/compat',
			},
			// https://cn.vitejs.dev/config/#resolve-extensions
			extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
		},
		// vite 相关配置
		server: {
			port: { development: 2024, test: 2025 }[mode],
			host: true,
			open: true,
			proxy: {
				// https://cn.vitejs.dev/config/#server-proxy
				'/api': {
					target: env.BASE_API,
					changeOrigin: true,
					rewrite: (p) => p.replace(/^\/api/, ''),
				},
			},
		},
		build: {
			// minify: 'terser',
			reportCompressedSize: false, // 启用/禁用 gzip 压缩大小报告
			// modulePreload: false, // { polyfill: false }
			// modulePreload: { polyfill: false },
			terserOptions: {
				compress: {
					//生产环境时移除console
					drop_console: mode == 'production',
					drop_debugger: mode == 'production',
				},
			},
			rollupOptions: {
				output: {
					assetFileNames: 'assets/[ext]/[name]-[hash][extname]',
					chunkFileNames: 'assets/js/[name]-[hash].js',
					manualChunks: (id) => {
						if (id.includes('assets/icons/svg')) {
							return 'icons-svg'
						} else if (['quill-image-resize-module', 'Quill'].some((e) => id.includes(e))) {
							return 'quill-image-resize-module'
						} else if (id.includes('node_modules')) {
							return id.split(/node_modules\/(\.store\/)??/)[1]?.split?.('/')[0]
						}
					},
				},
			},
		},
		plugins: [
			vue({}),
			createSvgIconsPlugin({
				iconDirs: [path.resolve(process.cwd(), 'src/assets/icons/svg')],
				symbolId: 'icon-[dir]-[name]',
			}),
			ViteImageOptimizer({
				png: {
					// https://sharp.pixelplumbing.com/api-output#png
					quality: 100,
				},
				jpeg: {
					quality: 100,
				},
				jpg: {
					quality: 100,
				},
			}),
			AutoImport({
				imports: ['vue', 'vue-router'],
				// // 可以选择auto-import.d.ts生成的位置
				// dts: 'src/auto-import.d.ts',
				// resolvers: [ElementPlusResolver()],
			}),
			vitePluginCompression({
				// disable: false, // 是否禁用压缩，默认为 false
				threshold: 10000, // 启用压缩的文件大小限制，单位是字节，默认为 0, 对大于 10kb 的文件进行压缩
				// filter：过滤器，对哪些类型的文件进行压缩，默认为 ‘/.(js|mjs|json|css|html)$/i’
				// verbose: true：是否在控制台输出压缩结果，默认为 true
				// deleteOriginFile：压缩后是否删除原文件，默认为 false
				// algorithm：采用的压缩算法，默认是 gzip
				// ext：生成的压缩包后缀
			}),

			process.env.npm_config_report &&
				rollupPluginVisualizer({
					emitFile: false, //使用 emitFile 生成文件。 属性为 true，打包后的分析文件会出现在打包好的文件包下；设置为 false ，则会出现在项目根目录下
					filename: 'report.html', //生成分析网页文件名
					open: false, //在默认用户代理中打开生成的文件
					gzipSize: true, //从源代码中收集 gzip 大小并将其显示在图表中
					// brotliSize: true, //从源代码中收集 brotli 大小并将其显示在图表中
				}),
		],
	}
})
