package ru.andrewkir.developerslifegifclient.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_tab.*
import ru.andrewkir.developerslifegifclient.R
import ru.andrewkir.developerslifegifclient.data.api.ApiBuilder
import ru.andrewkir.developerslifegifclient.data.api.ApiService
import ru.andrewkir.developerslifegifclient.data.api.PostsApi
import ru.andrewkir.developerslifegifclient.databinding.FragmentTabBinding
import ru.andrewkir.developerslifegifclient.utils.ResponseWithStatus
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum
import ru.andrewkir.developerslifegifclient.utils.ViewModelFactory

class TabFragment : Fragment() {

    private lateinit var binding: FragmentTabBinding

    private lateinit var currentSection: SectionsEnum
    private lateinit var viewModel: PostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        processArguments()
        setupViewModel()

        observePost()
        observeError()
        observeButtonsVisibility()

        setupButtons()

        binding.gifHolder.setOnClickListener {
            viewModel.nextPost()
        }

        binding.textView.setOnClickListener {
            viewModel.previousPost()
        }
    }

    private fun processArguments() {
        var pageNumber = 0
        arguments?.run {
            pageNumber = getInt(PAGE_NUMBER, 0)
        }
        currentSection = SectionsEnum.values()[pageNumber]
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory(PostsApi(ApiBuilder.apiService))).get(
                PostsViewModel::class.java
            )
        viewModel.init(currentSection)
    }

    private fun observePost() {
        context?.let {
            viewModel.postLiveData.observe(viewLifecycleOwner, { post ->
                if (post == null) {
                    showErrorPicture()
                    binding.textView.text = "Посты закончились :("
                } else post.run {
                    binding.loadingBar.visibility = View.VISIBLE
                    Glide.with(it)
                        .asGif()
                        .load(
                            gifURL?.replace(
                                "http://",
                                "https://"
                            )
                        )
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE) //TODO CHECK after
                        .listener(object : RequestListener<GifDrawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Toast.makeText(
                                    context,
                                    "Не удалось загрузить GIF",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: GifDrawable?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.loadingBar.visibility = View.GONE
                                return false
                            }
                        })
                        .transition(withCrossFade())
                        .into(binding.gifHolder)
                    binding.textView.text = post.description
                }
            })
        }
    }

    private fun observeError() {
        binding.loadingBar.visibility = View.GONE
        viewModel.errorResponse.observe(viewLifecycleOwner, { error ->
            showErrorPicture()
            if (error.isNetworkFailure) {
                Toast.makeText(context, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, error.body?.string(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeButtonsVisibility() {
        viewModel.backButtonVisibility.observe(viewLifecycleOwner, {
            backButton.isEnabled = it
        })
        viewModel.forwardButtonVisibility.observe(viewLifecycleOwner, {
            forwardButton.isEnabled = it
        })
    }

    private fun showErrorPicture() {
        context?.let {
            Glide.with(it)
                .load(R.drawable.ic_no_image)
                .transition(withCrossFade())
                .into(binding.gifHolder)
        }
    }

    private fun setupButtons() {
        binding.let {
            forwardButton.setOnClickListener { viewModel.nextPost() }
            backButton.setOnClickListener { viewModel.previousPost() }
        }
    }

    companion object {
        private const val PAGE_NUMBER = "page_number"

        fun newInstance(pageNumber: Int): TabFragment {
            val args = Bundle()
            args.putInt(PAGE_NUMBER, pageNumber)
            val fragment = TabFragment()
            fragment.arguments = args
            Log.d("CREATED FRAGMENT", "CREATED")
            return fragment
        }
    }
}